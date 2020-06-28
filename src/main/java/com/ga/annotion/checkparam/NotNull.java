package com.ga.annotion.checkparam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zelei.fan
 * @date 2020/3/26 11:44
 * @description 自定义注解，校验属性非空
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NotNull {

    String value() default "field is null";
}
