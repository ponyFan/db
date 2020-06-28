package com.ga.common.sqoop;

import com.ga.common.sqoop.entity.Mysql2HdfsModel;
import org.apache.hadoop.conf.Configuration;
import org.apache.sqoop.Sqoop;
import org.apache.sqoop.tool.SqoopTool;
import org.apache.sqoop.util.OptionsFileUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author zelei.fan
 * @date 2020/5/22 17:48
 * @description
 */
@Service
public class SqoopServiceImpl implements SqoopService {

    @Override
    public void mysql2hdfs(Mysql2HdfsModel model) throws Exception {
        SqoopUtils.sql2hdfs(model, model.getUrl());
    }

    @Override
    public void oracle2hdfs(Mysql2HdfsModel model) throws Exception {
        SqoopUtils.sql2hdfs(model, model.getOracleUrl());
    }

    @Override
    public void mysql2hdfsIncremental(Mysql2HdfsModel model) throws Exception {
        //1、将密码写到hdfs上
        //2、创建任务
        //3、执行任务
    }

    @Override
    public SqoopBean mysql2hdfs(String jdbc, String driver, String username, String password, String table, int m, String targetdir, String putlocation) throws Exception {
        String[] args = new String[] {
                "--connect", jdbc,
                "--driver", driver,
                "-username", username,
                "-password", password,
                "--table", table,
                "-m", String.valueOf(m),
                "--target-dir", targetdir,
        };
        SqoopBean sqoopBean = new SqoopBean();
        String[] expandArguments = OptionsFileUtil.expandArguments(args);
        SqoopTool tool = SqoopTool.getTool("import");
        Configuration conf = new Configuration();
        conf.set("fs.default.name", putlocation);//设置HDFS服务地址
        Configuration loadPlugins = SqoopTool.loadPlugins(conf);
        Sqoop sqoop = new Sqoop((com.cloudera.sqoop.tool.SqoopTool) tool, loadPlugins);
        sqoopBean.setI(Sqoop.runSqoop(sqoop,expandArguments));
        sqoopBean.setTs(new Timestamp(new Date().getTime()));
        return sqoopBean;
    }

    @Override
    public SqoopBean mysql2Hbase(String jdbc, String driver, String username, String password, String mysqlTable, String hbaseTableName, String columnFamily, String rowkey, int m) throws Exception {
        String[] args = new String[] {
                "--connect",jdbc,
                "--driver",driver,
                "-username",username,
                "-password",password,
                "--table",mysqlTable,
                "--hbase-table",hbaseTableName,
                "--column-family",columnFamily,
                "--hbase-create-table",
                "--hbase-row-key",rowkey,
                "-m",String.valueOf(m),
        };
        SqoopBean sqoopBean = new SqoopBean();
        String[] expandArguments = OptionsFileUtil.expandArguments(args);
        SqoopTool tool = SqoopTool.getTool("import");
        Configuration conf = new Configuration();
        Configuration loadPlugins = SqoopTool.loadPlugins(conf);
        Sqoop sqoop = new Sqoop((com.cloudera.sqoop.tool.SqoopTool) tool, loadPlugins);
        sqoopBean.setI(Sqoop.runSqoop(sqoop,expandArguments));
        sqoopBean.setTs(new Timestamp(new Date().getTime()));
        return sqoopBean;
    }
}
