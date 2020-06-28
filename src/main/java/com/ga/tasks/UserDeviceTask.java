package com.ga.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
}
