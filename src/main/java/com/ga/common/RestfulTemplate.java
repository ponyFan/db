package com.ga.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author zelei.fan
 * @date 2019/12/30 16:56
 * @description
 */
@Component
public class RestfulTemplate {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * restTemplate初始化
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(5 * 1000);
        factory.setConnectTimeout(5 * 1000);
        factory.setReadTimeout(5 * 1000);
        return new RestTemplate(factory);
    }

    /**
     * rpc调用，设置异常重试次数
     * @param url
     * @param method
     * @param data
     * @param clazz
     * @return
     */
    @Retryable(value = Exception.class)
    public Object call(String url, HttpMethod method, Object data, Class clazz) {
        HttpEntity<Object> entity = new HttpEntity<>(data);
        ResponseEntity responseEntity = restTemplate.exchange(url, method, entity, clazz);
        return responseEntity.getBody();
    }
}
