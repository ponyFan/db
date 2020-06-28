package com.ga.enums;

/**
 * @author zelei.fan
 * @date 2019/11/22 9:12
 * @description hbase过滤器枚举
 */
public enum HbaseFilterEnum {
    ROW,// 根据rowkey过滤
    FAMILY,// 根据family过滤
    QUALIFIER,// 根据列名过滤
    VALUE,// 根据值过滤
    TIMESTAMP,// 根据版本过滤
    SINGLE_COL, //单列过滤
    SINGLE_COL_EXCLUDE,// 单例值排除过滤
    ROW_PREFIX,// row前缀过滤
    ROW_SUFFIX,// row后缀过滤
    ROW_CONTAINS,// row包含过滤
    COLUMN_PREFIX// 列前缀过滤
}
