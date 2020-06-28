package com.ga.annotion.multisource;

/**
 * @author zelei.fan
 * @date 2020/5/6 9:56
 * @description
 */
public class DataSourceContextHolder {

    public static final String DEFAULT_DS = "mysql";

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDB(String dbType){
        contextHolder.set(dbType);
    }

    public static String getDB(){
        return contextHolder.get();
    }

    public static void clearDB(){
        contextHolder.remove();
    }
}
