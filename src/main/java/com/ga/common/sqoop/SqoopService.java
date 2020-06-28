package com.ga.common.sqoop;

import com.ga.common.sqoop.entity.Mysql2HdfsModel;

/**
 * @author zelei.fan
 * @date 2020/5/22 17:46
 * @description
 */
public interface SqoopService {

    /**
     * mysql同步到hdfs
     * @param model
     */
    void mysql2hdfs(Mysql2HdfsModel model) throws Exception;

    /**
     * oracle同步到hdfs
     * @param model
     * @throws Exception
     */
    void oracle2hdfs(Mysql2HdfsModel model) throws Exception;

    /**
     * mysql增量同步，按指定列的最新值
     * @param model
     * @throws Exception
     */
    void mysql2hdfsIncremental(Mysql2HdfsModel model) throws  Exception;

    /**
     * mysql同步到hdfs，文件格式默认parquet
     * @param jdbc  mysql连接url
     * @param driver jdbc驱动类
     * @param username  用户名
     * @param password  密码
     * @param table 需要同步的表名
     * @param m map并行度
     * @param targetdir hdfs目标目录
     * @param putlocation  hdfs 服务地址
     * @return
     * @throws Exception
     */
    SqoopBean mysql2hdfs(String jdbc, String driver, String username, String password, String table, int m, String targetdir, String putlocation) throws Exception;

    //mysql到hbase
    SqoopBean mysql2Hbase(String jdbc, String driver, String username, String password, String mysqlTable, String hbaseTableName, String columnFamily, String rowkey, int m) throws Exception;

}
