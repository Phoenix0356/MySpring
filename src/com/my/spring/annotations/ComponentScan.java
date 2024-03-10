package com.my.spring.annotations;

import com.my.spring.scanner.BeanNameGenerator;
import com.my.spring.scanner.DefaultBeanNameGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {
    //scan path
    String value() default "";
    Class<? extends BeanNameGenerator> beanNameGenerator() default DefaultBeanNameGenerator.class;
}
