package com.my.spring.Interfaces;

public interface BeanPostProcessor {
    public void postProcessorBeforeInitialization(String beanName,Object bean);
    public void postProcessorAfterInitialization(String beanName,Object bean);

}
