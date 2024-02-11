package com.my.spring;

import com.my.spring.Annotations.Component;
import com.my.spring.Annotations.ComponentScan;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class MyApplicationContext {
    private final Class<?> configClass;
    private final ClassLoader classLoader;

    public MyApplicationContext(Class<?> configClass){
        this.configClass = configClass;
        this.classLoader = MyApplicationContext.class.getClassLoader();

        //scan
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            scan();
        }

    }
    public void scan(){
        File file = new File(getResourceUrl());
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files == null) return;
            try {
                for (File f : files) {
                    String className = getClassName(f);
                    createBean(className);
                }
            } catch (URISyntaxException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getResourceUrl(){
        ComponentScan componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");
        return Objects.requireNonNull(classLoader.getResource(path)).getFile();

    }

    public String getClassName(File file) throws URISyntaxException {
        String absolutePath = file.getAbsolutePath();
        String className = null;
        if (absolutePath.endsWith(".class")) {
            URL classRootURL = classLoader.getResource("");
            String classRootPath = new File(classRootURL.toURI()).getAbsolutePath();
            className = absolutePath.
                   substring(classRootPath.length()+1, absolutePath.indexOf(".class")).
                   replace("\\", ".");
        }
        return className;
    }
    public void createBean(String className) throws ClassNotFoundException {
        if (className == null) return;
        Class<?> clazz;
        clazz = classLoader.loadClass(className);
        if (clazz.isAnnotationPresent(Component.class)) {
            //bean
        }
    }


    public Object getBean(Class<?> beanClass){
        return null;
    }
}
