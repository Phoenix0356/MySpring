package com.my.service;


import com.my.spring.Annotations.Autowired;
import com.my.spring.Annotations.Component;
import com.my.spring.Annotations.Scope;
import com.my.spring.Interfaces.BeanNameAware;
import com.my.spring.Interfaces.InitializeBean;

@Component
@Scope("prototype")
public class MyService implements BeanNameAware, InitializeBean, MyServiceInterface {
    @Autowired
    DiService diService;

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(beanName+" initialized");
    }
    @Override
    public void test(){
        System.out.println(diService);
    }



}
