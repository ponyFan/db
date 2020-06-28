package com.ga.annotion.time;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author zelei.fan
 * @date 2020/3/26 15:40
 * @description
 */
@Aspect
@Component
@Log4j2
public class TimingProcess {

    @Pointcut("@annotation(com.ga.annotion.time.Timing)")
    public void interceptorTiming(){}

    @Around("interceptorTiming()")
    public void timingMethod(ProceedingJoinPoint joinPoint){
        MethodSignature method = (MethodSignature)joinPoint.getSignature();
        long start = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            log.info(method.getName() + " spend time " + time + "ms");
        }
    }
}
