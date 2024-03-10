package com.my.service;

import com.my.spring.scanner.BeanNameGenerator;

public class MyBeanNameGenerator implements BeanNameGenerator {

    @Override
    public String generateBeanName(Class<?> clazz) {
        return "testGenerator"+clazz.getSimpleName();
    }
}
