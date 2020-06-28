package com.ga.annotion.checkparam;

import com.ga.common.ServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author zelei.fan
 * @date 2020/3/26 11:48
 * @description 切面，处理非空校验的注解逻辑
 */
@Aspect
@Component
public class CheckNullProcess {

    @Pointcut("@annotation(com.ga.annotion.checkparam.Check)")
    public void checkNull(){}

    /**
     * 此注解（@Check）只适用校验对象
     * @param joinPoint
     */
    @Before("checkNull()")
    public void check(JoinPoint joinPoint){
        /*获取注解所在方法的参数*/
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Class<?> aClass = arg.getClass();
            /*根据参数实例遍历其属性，根据有注解的属性，判断是否为空，没注解的默认不需要校验*/
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null) {
                    Object o = ReflectionUtils.getField(field, arg);
                    if (null == o){
                        throw new ServiceException(201, notNull.value());
                    }
                }
            }
        }
    }
}
