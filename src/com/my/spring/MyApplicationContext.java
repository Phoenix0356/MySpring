package com.my.spring;

import com.my.spring.annotations.Autowired;
import com.my.spring.annotations.Component;
import com.my.spring.annotations.ComponentScan;
import com.my.spring.annotations.Scope;
import com.my.spring.awares.BeanNameAware;
import com.my.spring.postprocessor.BeanPostProcessor;
import com.my.spring.initializer.InitializeBean;
import com.my.spring.scanner.BeanNameGenerator;
import com.my.spring.scanner.DefaultBeanNameGenerator;
import com.my.spring.scanner.Scanner;
import com.my.spring.util.BeanUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private final Class<?> configClass;
    private final ClassLoader classLoader;

    private final Scanner scanner;

    private BeanNameGenerator beanNameGenerator;

    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //单例池
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final List<BeanPostProcessor> BeanPostProcessorList = new ArrayList<>();
    public final HashSet<String> creatingBeanSet = new HashSet<>();
    //二级缓存
    public final ConcurrentHashMap<String,Object> earlySingletonBeans = new ConcurrentHashMap<>();


    public MyApplicationContext(Class<?> configClass){
        this.configClass = configClass;
        this.scanner = new Scanner();
        this.classLoader = MyApplicationContext.class.getClassLoader();
        

        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);

            generateBeanNameGenerator(componentScanAnnotation);

            //scan
            scan(componentScanAnnotation);

            //create singleton
            for (String beanName : beanDefinitionMap.keySet()) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition.getScope().equals("singleton")){
                    if (singletonObjects.get(beanName) == null) {
                        Object bean = createBean(beanName, beanDefinition);
                        singletonObjects.put(beanName, bean);
                    }
                }
            }
        }
    }

    private void generateBeanNameGenerator(ComponentScan componentScan){
        Class<? extends BeanNameGenerator> generatorClass = componentScan.beanNameGenerator();
        boolean useDefaultNameGenerator = BeanNameGenerator.class == generatorClass;
        this.beanNameGenerator = (BeanNameGenerator) (useDefaultNameGenerator?
                        BeanUtil.instantiateBean(DefaultBeanNameGenerator.class):
                        BeanUtil.instantiateBean(generatorClass));
    }

    private void scan(ComponentScan componentScanAnnotation){
        String path = componentScanAnnotation.value().replace(".","/");
        String beansAbsolutePath = Objects.requireNonNull(classLoader.getResource(path)).getFile();

        List<File> files = obtainAllFiles(beansAbsolutePath);
        try {
            for (File f : files) {
                String className = obtainClassName(f);
                createBeanDefinition(className);
            }
        } catch (URISyntaxException | ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    //bfs获取所有文件
    private List<File> obtainAllFiles(String beansAbsolutePath){
        List<File> classFileList = new ArrayList<>();
        File entranceFileObject = new File(beansAbsolutePath);

        Queue<File> fileQueue = new ArrayDeque<>();
        fileQueue.offer(entranceFileObject);

        while (!fileQueue.isEmpty()){
            File curFileObject = fileQueue.poll();
            if (curFileObject.isFile()) classFileList.add(curFileObject);
            else {
                File[] childFileList = curFileObject.listFiles();
                if (childFileList != null) {
                    for (File file : childFileList) {
                        fileQueue.offer(file);
                    }
                }
            }
        }
        return classFileList;
    }

    private String obtainClassName(File file) throws URISyntaxException {
        String absolutePath = file.getAbsolutePath();
        String className = null;
        if (absolutePath.endsWith(".class")) {
            URL classRootURL = classLoader.getResource("");
            String classRootPath = new File(classRootURL.toURI()).getAbsolutePath();
            className = absolutePath.
                    substring(classRootPath.length() + 1, absolutePath.indexOf(".class")).
                    replace("\\", ".");
        }
        return className;
    }
    //scan
    private void createBeanDefinition(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (className == null) {
            throw new NullPointerException();
        }
        Class<?> clazz;
        clazz = classLoader.loadClass(className);
        if (clazz.isAnnotationPresent(Component.class)) {

            //instance the beanPostProcessor
            if (BeanPostProcessor.class.isAssignableFrom(clazz)){
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                this.BeanPostProcessorList.add(beanPostProcessor);
            }

            String beanName = beanNameGenerator.generateBeanName(clazz);

            //BeanDefinition
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setType(clazz);
            if (clazz.isAnnotationPresent(Scope.class)){
                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                beanDefinition.setScope(scopeAnnotation.value());
            }else {
                beanDefinition.setScope("singleton");
            }
            this.beanDefinitionMap.put(beanName,beanDefinition);
        }
    }

    private Object createBean(String beanName,BeanDefinition beanDefinition){
        Class<?> clazz = beanDefinition.getType();
        beanName = this.beanNameGenerator.generateBeanName(clazz);
        Object instance;
        try {
            if (creatingBeanSet.contains(beanName)){
                instance = earlySingletonBeans.get(beanName);

            }else {
                instance = clazz.getDeclaredConstructor().newInstance();

                creatingBeanSet.add(beanName);
                earlySingletonBeans.put(beanName,instance);

                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        field.setAccessible(true);
                        Class<?> fieldBeanClass = field.getType();

                        String fieldBeanName = this.beanNameGenerator.generateBeanName(fieldBeanClass);

                        field.set(instance, getBean(fieldBeanName));
                    }
                }

                //Aware callback
                if (instance instanceof BeanNameAware) {
                    ((BeanNameAware) instance).setBeanName(beanName);
                }

                //before init operation
                for (BeanPostProcessor beanPostProcessor : BeanPostProcessorList) {
                    instance = beanPostProcessor.postProcessorBeforeInitialization(beanName, instance);
                }

                //initialize
                if (instance instanceof InitializeBean) {
                    ((InitializeBean) instance).afterPropertiesSet();
                }

                //after init operation
                for (BeanPostProcessor beanPostProcessor : BeanPostProcessorList) {
                    instance = beanPostProcessor.postProcessorAfterInitialization(beanName, instance);
                }
            }

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e){
                throw new RuntimeException(e);
        }

        creatingBeanSet.remove(beanName);
        earlySingletonBeans.remove(beanName);

        return instance;
    }

    public Object getBean(String beanName){
       BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
       if (beanDefinition == null){
           throw new NullPointerException();
       }else {
           String scope = beanDefinition.getScope();
           if (scope.equals("singleton")){
               //get from singleton pool
               Object bean = singletonObjects.get(beanName);
               if (bean == null){
                   Object newBean = createBean(beanName,beanDefinition);
                   singletonObjects.put(beanName,newBean);
                   return newBean;
               }
               return bean;
           }else {
               //create bean
               return createBean(beanName,beanDefinition);
           }

       }
    }
}
