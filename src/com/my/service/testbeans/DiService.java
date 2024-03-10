package com.my.service.testbeans;

import com.my.spring.annotations.Autowired;
import com.my.spring.annotations.Component;
import com.my.spring.annotations.Scope;

@Component
public class DiService {
    @Autowired
    MyService myService;
    @Autowired
    UserService userService;

    public void test(){
        System.out.println("diService: "+this);
        System.out.println("diService: "+myService);
        System.out.println("diService: "+userService);

    }
}
