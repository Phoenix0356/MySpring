package com.my.spring.util;

import java.lang.reflect.InvocationTargetException;

public class BeanUtil {
    public static Object instantiateBean(Class<?> clazz) {
        Object object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
