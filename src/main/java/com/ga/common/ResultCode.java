package com.ga.common;

/**
 * @author zelei.fan
 * @date 2019/12/19 14:12
 * @description
 */
public interface ResultCode {

    int SUCCESS = 200;
    int ERROR = 201;
    //时间转换错误码
    int TIME_PARES_ERROR = 202;
    //文件读取错误
    int FILE_READ_ERROR = 203;
}
