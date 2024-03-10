package com.my.service.testbeans;


import com.my.spring.annotations.Autowired;
import com.my.spring.annotations.Component;
import com.my.spring.annotations.Scope;
import com.my.spring.awares.BeanNameAware;
import com.my.spring.initializer.InitializeBean;

@Component
@Scope("singleton")
public class MyService implements BeanNameAware, InitializeBean{
    @Autowired
    DiService diService;

    @Autowired
    UserService userService;

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName(){
        return beanName;
    }

    @Override
    public void afterPropertiesSet() {
//        System.out.println(beanName+" initialized");
    }

    public void test(){
        System.out.println("myService: "+this);
        System.out.println("myService: "+diService);
        System.out.println("myService: "+userService);
    }



}
