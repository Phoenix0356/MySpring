package com.my.spring.postprocessor;

public interface BeanPostProcessor {
    public Object postProcessorBeforeInitialization(String beanName,Object bean);
    public Object postProcessorAfterInitialization(String beanName,Object bean);

}
