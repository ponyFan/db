package com.ga.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zelei.fan
 * @date 2019/12/16 14:32
 * @description
 */
@Component
public class UserDeviceTask {

    @Scheduled(cron = "*/5 * * * * ?")
    public void getDeviceUser(){

    }

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger();

        int arr[] = {1,8,45,6,12,44};
        //得到串行流
        /*Arrays.stream(arr).sequential();*/
        //得到并行流
        Arrays.stream(arr).filter(value -> value==2).parallel();
    }
}
