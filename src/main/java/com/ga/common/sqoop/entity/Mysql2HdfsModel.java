package com.ga.common.sqoop.entity;

import com.alibaba.fastjson.JSON;

import javax.validation.constraints.NotNull;

/**
 * @author zelei.fan
 * @date 2020/6/1 15:30
 * @description
 */
public class Mysql2HdfsModel extends MysqlConnectModel {

    /**
     * hdfs集群url
     */
    @NotNull(message = "hdfs集群url不能为空")
    private String defaultFS;

    /**
     * 同步大屏hdfs上的目录
     */
    @NotNull(message = "目标目录不能为空")
    private String targetDir;

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
