package com.my.service;

import com.my.spring.annotations.ComponentScan;

@ComponentScan(value = "com.my.service",beanNameGenerator = MyBeanNameGenerator.class)
public class AppConfig {
}
