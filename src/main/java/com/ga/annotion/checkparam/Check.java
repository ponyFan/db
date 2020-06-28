package com.ga.annotion.checkparam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zelei.fan
 * @date 2020/3/26 11:46
 * @description 自定义注解，标志需要校验的方法或属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Check {

    String msg() default "";
}
