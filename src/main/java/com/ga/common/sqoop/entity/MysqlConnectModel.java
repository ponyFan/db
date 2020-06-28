package com.ga.common.sqoop.entity;

import com.alibaba.fastjson.JSON;

import javax.validation.constraints.NotNull;

/**
 * @author zelei.fan
 * @date 2020/6/1 14:47
 * @description mysql连接信息
 */
public class MysqlConnectModel {

    @NotNull(message = "ip 不能为空")
    private String ip;

    private String port = "3306";

    @NotNull(message = "数据库名不能为空")
    private String database;

    @NotNull(message = "表名不能为空")
    private String table;

    @NotNull(message = "用户名不能为空")
    private String user;

    @NotNull(message = "密码不能为空")
    private String password;

    private String query;

    /**
     * 根据某一列增量
     */
    private String checkColumn;

    /**
     * 增量起始值
     */
    private Object lastValue;

    private String mapTask = "1";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCheckColumn() {
        return checkColumn;
    }

    public void setCheckColumn(String checkColumn) {
        this.checkColumn = checkColumn;
    }

    public Object getLastValue() {
        return lastValue;
    }

    public void setLastValue(Object lastValue) {
        this.lastValue = lastValue;
    }

    public String getMapTask() {
        return mapTask;
    }

    public void setMapTask(String mapTask) {
        this.mapTask = mapTask;
    }

    /**
     * 拼接url
     * @return
     */
    public String getUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append("jdbc:mysql://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/")
                .append(database)
                .append("?serverTimezone=Hongkong&useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        return builder.toString();
    }

    /**
     * 拼接oracle url
     * @return
     */
    public String getOracleUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append("jdbc:oracle:thin:@")
                .append(ip)
                .append(":")
                .append(port)
                .append(":")
                .append(database);
        return builder.toString();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
