package com.my.service.testbeans;

import com.my.spring.annotations.Autowired;
import com.my.spring.annotations.Component;

@Component
public class UserService {
    @Autowired
    MyService myService;
    @Autowired
    DiService diService;

    public void test(){
        System.out.println("UserService: "+this);
        System.out.println("UserService: "+myService);
        System.out.println("UserService: "+diService);
    }
}
