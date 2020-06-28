package com.ga.common;

/**
 * @author zelei.fan
 * @date 2019/12/18 17:18
 * @description 自定义异常类
 */
public class ServiceException extends RuntimeException {

   private int code;
   private String msg;

   public ServiceException(int code, String msg){
       this.code = code;
       this.msg = msg;
   }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
