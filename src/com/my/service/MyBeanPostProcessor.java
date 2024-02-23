package com.my.service;

import com.my.spring.Annotations.Component;
import com.my.spring.Interfaces.BeanPostProcessor;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void postProcessorBeforeInitialization(String beanName, Object bean) {

    }

    @Override
    public void postProcessorAfterInitialization(String beanName, Object bean) {
        if (bean.getClass().isAssignableFrom(MyService.class)){
            ((MyService) bean).setBeanName("postTest");
        }
    }
}
