package com.my.spring.scanner;

import com.my.spring.BeanDefinition;
import com.my.spring.annotations.Component;

import java.beans.Introspector;

public class DefaultBeanNameGenerator implements BeanNameGenerator{

    @Override
    public String generateBeanName(Class<?> clazz){
        Component component = clazz.getAnnotation(Component.class);
        String beanName = component.value();

        if (beanName.isEmpty()) return Introspector.decapitalize(clazz.getSimpleName());

        return beanName;
    }
}
