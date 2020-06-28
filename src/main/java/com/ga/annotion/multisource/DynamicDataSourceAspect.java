package com.ga.annotion.multisource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author zelei.fan
 * @date 2020/5/6 10:17
 * @description
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(com.ga.annotion.multisource.DS)")
    public void beforeSwitchDS(JoinPoint joinPoint){
        Class<?> aClass = joinPoint.getTarget().getClass();
        String name = joinPoint.getSignature().getName();
        Class[] types = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        String datasource = DataSourceContextHolder.DEFAULT_DS;
        try {
            Method method = aClass.getMethod(name, types);
            if (method.isAnnotationPresent(DS.class)){
                DS annotation = method.getAnnotation(DS.class);
                datasource = annotation.value();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        DataSourceContextHolder.setDB(datasource);
    }

    @After("@annotation(com.ga.annotion.multisource.DS)")
    public void afterSwitchDS(){
        DataSourceContextHolder.clearDB();
    }
}
