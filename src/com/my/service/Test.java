package com.my.service;

import com.my.service.testbeans.DiService;
import com.my.service.testbeans.MyService;
import com.my.service.testbeans.UserService;
import com.my.spring.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        MyService myService = (MyService) myApplicationContext.getBean("testGeneratorMyService");
        DiService diService = (DiService) myApplicationContext.getBean("testGeneratorDiService");
        UserService userService =(UserService) myApplicationContext.getBean("testGeneratorUserService");

        System.out.println(myService.getBeanName());

        myService.test();
        diService.test();
        userService.test();

//        System.out.println(myApplicationContext.getBean("testGeneratorMyService"));
//        System.out.println(myApplicationContext.getBean("testGeneratorMyService"));
//        System.out.println(myApplicationContext.getBean("testGeneratorMyService"));
//        System.out.println(myApplicationContext.getBean("testGeneratorMyService"));

    }
}
