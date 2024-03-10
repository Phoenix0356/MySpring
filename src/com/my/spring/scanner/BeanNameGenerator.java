package com.my.spring.scanner;

import com.my.spring.BeanDefinition;

public interface BeanNameGenerator {
    public String generateBeanName(Class<?> clazz);


}
