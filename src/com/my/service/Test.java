package com.my.service;

import com.my.spring.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        MyService myService = (MyService) myApplicationContext.getBean("myService");
        myService.test();
        myService.afterPropertiesSet();
        System.out.println(myApplicationContext.getBean("myService"));
        System.out.println(myApplicationContext.getBean("myService"));
        System.out.println(myApplicationContext.getBean("myService"));

    }
}
