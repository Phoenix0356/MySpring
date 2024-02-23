package com.my.spring;

import com.my.spring.Annotations.Autowired;
import com.my.spring.Annotations.Component;
import com.my.spring.Annotations.ComponentScan;
import com.my.spring.Annotations.Scope;
import com.my.spring.Interfaces.BeanNameAware;
import com.my.spring.Interfaces.BeanPostProcessor;
import com.my.spring.Interfaces.InitializeBean;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private final Class<?> configClass;
    private final ClassLoader classLoader;


    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjectsPool = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> BeanPostProcessorList = new ArrayList<>();

    public MyApplicationContext(Class<?> configClass){
        this.configClass = configClass;
        this.classLoader = MyApplicationContext.class.getClassLoader();

        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //scan
            scan();

            //create singleton
            for (String beanName : beanDefinitionMap.keySet()) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition.getScope().equals("singleton")){
                    Object bean = createBean(beanName,beanDefinition);
                    singletonObjectsPool.put(beanName,bean);
                }
            }
        }
    }
    private void scan(){
        File file = new File(getBeansAbsolutelyPath());
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files == null) System.out.println("No Beans Found!!!");;
            try {
                for (File f : files) {
                    String className = getClassName(f);
                    createBeanDefinition(className);
                }
            } catch (URISyntaxException | ClassNotFoundException | InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //scan
    private String getBeansAbsolutelyPath(){
        ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");
        return Objects.requireNonNull(classLoader.getResource(path)).getFile();
    }
    //scan
    private String getClassName(File file) throws URISyntaxException {
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

            Component component = clazz.getAnnotation(Component.class);
            String beanName = component.value();

            if (beanName.isEmpty()){
                beanName = Introspector.decapitalize(clazz.getSimpleName());
            }

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
        Class clazz = beanDefinition.getType();
        Object instance;
        try {
            instance = clazz.getConstructor().newInstance();

            //dependence injection
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
                    field.set(instance,getBean(field.getName()));
                }
            }

            //Aware callback
            if (instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //before init operation
            for (BeanPostProcessor beanPostProcessor : BeanPostProcessorList) {
                beanPostProcessor.postProcessorBeforeInitialization(beanName,instance);
            }

            //initialize
            if (instance instanceof InitializeBean){
                ((InitializeBean) instance).afterPropertiesSet();
            }

            //after init operation
            for (BeanPostProcessor beanPostProcessor : BeanPostProcessorList) {
                beanPostProcessor.postProcessorAfterInitialization(beanName,instance);
            }


        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
               Object bean = singletonObjectsPool.get(beanName);
               if (bean == null){
                   Object newBean = createBean(beanName,beanDefinition);
                   singletonObjectsPool.put(beanName,beanDefinition);
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
