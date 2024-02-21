package com.my.spring;

public class BeanDefinition {
    private Class type;
    private String scope;
    public BeanDefinition(){}

    public Class getType(){
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
