package com.my.service;

import com.my.spring.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        Object myService = myApplicationContext.getBean(myService.class);
    }
}
