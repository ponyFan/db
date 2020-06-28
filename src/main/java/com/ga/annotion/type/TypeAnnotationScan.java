package com.ga.annotion.type;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zelei.fan
 * @date 2020/5/11 10:28
 * @description
 */
public class TypeAnnotationScan implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public TypeAnnotationScan annotationScan(){
        return new TypeAnnotationScan();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("dddddddddddddddddddddddd");
    }
}
