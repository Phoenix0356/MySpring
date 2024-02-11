package com.my.spring;

import com.my.spring.Annotations.Component;
import com.my.spring.Annotations.ComponentScan;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;

public class MyApplicationContext {
    private final Class<?> configClass;
    private final ClassLoader classLoader;

    public MyApplicationContext(Class<?> configClass){
        this.configClass = configClass;
        this.classLoader = MyApplicationContext.class.getClassLoader();;

        //scan
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            scan();
        }

    }
    public void scan(){
        File file = new File(getResourceUrl());
        if (file.isDirectory()){
            File[] files = file.listFiles();
            try {
                for (File f : files) {
                    String className = getClassName(f);
                    createBean(className);
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getResourceUrl(){
        ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");
        return classLoader.getResource(path).getFile();

    }

    public String getClassName(File file) throws URISyntaxException {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith(".class")) {
            URL classRootURL = classLoader.getResource("");
            String classRootPath = new File(classRootURL.toURI()).getAbsolutePath();

            return absolutePath.
                    substring(classRootPath.length()+1, absolutePath.indexOf(".class")).
                    replace("\\", ".");
        }
        return null;
    }
    public void createBean(String className){
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (clazz.isAnnotationPresent(Component.class)) {
            //bean
        }
    }


    public Object getBean(Class<?> beanClass){

        return null;
    }
}
