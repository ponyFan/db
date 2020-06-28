package com.ga.entity.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author zelei.fan
 * @date 2019/11/28 10:36
 * @description
 */
public class BaseModel<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public BaseModel(int code, String message){
        this.code = code;
        this.message = message;
    }

    public BaseModel(int code, String message, T data){
        this(code, message);
        this.data = data;
    }

    public static BaseModel simpleSuccessModel(){
        return new BaseModel(200, "success");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
