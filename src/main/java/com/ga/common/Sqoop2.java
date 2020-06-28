package com.ga.common;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.*;
import org.apache.sqoop.submission.counter.Counter;
import org.apache.sqoop.submission.counter.CounterGroup;
import org.apache.sqoop.submission.counter.Counters;
import org.apache.sqoop.validation.Status;

import java.util.Arrays;
import java.util.UUID;


/**
 * @author zelei.fan
 * @date 2020/6/3 10:01
 * @description
 */
public class Sqoop2 {

    static SqoopClient client;

    public static MLink createMysqlLink() {
        MLink link = client.createLink("generic-jdbc-connector");
        // 随机生成名字,不能过长,否则会报错
        link.setName("mysql2");
        link.setCreationUser("haha");
        MLinkConfig linkConfig = link.getConnectorLinkConfig();
        linkConfig.getStringInput("linkConfig.connectionString").setValue("jdbc:mysql://192.168.9.22:3306/nist_bus");
        linkConfig.getStringInput("linkConfig.jdbcDriver").setValue("com.mysql.jdbc.Driver");
        linkConfig.getStringInput("linkConfig.username").setValue("root");
        linkConfig.getStringInput("linkConfig.password").setValue("root");
        // 这里必须指定 identifierEnclose, 他默认是双引号,也会报错
        linkConfig.getStringInput("dialect.identifierEnclose").setValue("`");
        Status status = client.saveLink(link);
        if (status.canProceed()) {
            System.out.println("Created Link with Link Name : " + link.getName());
            return link;
        } else {
            System.out.println("Something went wrong creating the link");
            return null;
        }
    }

    public static MLink createOracleLink() {
        MLink link = client.createLink("generic-jdbc-connector");
        // 随机生成名字,不能过长,否则会报错
        link.setName("oracle2");
        link.setCreationUser("haha");
        MLinkConfig linkConfig = link.getConnectorLinkConfig();
        linkConfig.getStringInput("linkConfig.connectionString").setValue("jdbc:oracle:thin:@db.nist.ac.cn:1521:orcl");
        linkConfig.getStringInput("linkConfig.jdbcDriver").setValue("oracle.jdbc.driver.OracleDriver");
        linkConfig.getStringInput("linkConfig.username").setValue("CTKYZ");
        linkConfig.getStringInput("linkConfig.password").setValue("CTKYZ");
        // 这里必须指定 identifierEnclose, 他默认是双引号,也会报错
        linkConfig.getStringInput("dialect.identifierEnclose").setValue(" ");
        Status status = client.saveLink(link);
        if (status.canProceed()) {
            System.out.println("Created Link with Link Name : " + link.getName());
            return link;
        } else {
            System.out.println("Something went wrong creating the link");
            return null;
        }
    }


    public static MLink createHdfsLink() {
        MLink link = client.createLink("hdfs-connector");
        link.setName("hdfs2");
        link.setCreationUser("haha");
        MLinkConfig linkConfig = link.getConnectorLinkConfig();
        linkConfig.getStringInput("linkConfig.uri").setValue("hdfs://nist23:9000");
        linkConfig.getStringInput("linkConfig.confDir").setValue("/usr/local/hadoop-2.7.2/etc/hadoop");
        Status status = client.saveLink(link);
        if (status.canProceed()) {
            System.out.println("Created Link with Link Name : " + link.getName());
            return link;
        } else {
            System.out.println("Something went wrong creating the link");
            return null;
        }


    }

    public static String createMysql2HdfsJob(MLink fromLink, MLink toLink) {
        MJob job = client.createJob(fromLink.getName(), toLink.getName());
        job.setName("mysql2hdfs2");
        job.setCreationUser("haha");
        MFromConfig fromJobConfig = job.getFromJobConfig();

        fromJobConfig.getStringInput("fromJobConfig.schemaName").setValue("CTKYZ");
        fromJobConfig.getStringInput("fromJobConfig.tableName").setValue("T_PACKAGE_CHECK");
        fromJobConfig.getListInput("fromJobConfig.columnList").setValue(Arrays.asList( "ID", "STATIONNAME"));
        MToConfig toJobConfig = job.getToJobConfig();
        toJobConfig.getStringInput("toJobConfig.outputDirectory").setValue("hdfs://nist23:9000/sqoop114");
        toJobConfig.getEnumInput("toJobConfig.outputFormat").setValue("TEXT_FILE");
        toJobConfig.getEnumInput("toJobConfig.compression").setValue("NONE");
        toJobConfig.getBooleanInput("toJobConfig.overrideNullValue").setValue(true);
        MDriverConfig driverConfig = job.getDriverConfig();
        driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);

        Status status = client.saveJob(job);
        if (status.canProceed()) {
            System.out.println("Created Job with Job Name: " + job.getName());
            return job.getName();
        } else {
            System.out.println("Something went wrong creating the job");
            return null;
        }
    }

    public static String createMysql2HdfsAppendJob(MLink fromLink, MLink toLink) {
        MJob job = client.createJob(fromLink.getName(), toLink.getName());
        job.setName("mysql2hdfsAppend");
        job.setCreationUser("haha");
        MFromConfig fromJobConfig = job.getFromJobConfig();
        fromJobConfig.toString();
        fromJobConfig.getStringInput("fromJobConfig.schemaName").setValue("nist_bus");
        fromJobConfig.getStringInput("fromJobConfig.tableName").setValue("zabbix_process");
        fromJobConfig.getListInput("fromJobConfig.columnList").setValue(Arrays.asList( "item_id", "process_name", "ip"));
        fromJobConfig.getStringInput("incrementalRead.checkColumn").setValue("item_id");
        fromJobConfig.getStringInput("incrementalRead.lastValue").setValue("125");

        MToConfig toJobConfig = job.getToJobConfig();
        toJobConfig.toString();
        toJobConfig.getBooleanInput("toJobConfig.overrideNullValue").setValue(true);
        toJobConfig.getEnumInput("toJobConfig.outputFormat").setValue("PARQUET_FILE");
        toJobConfig.getBooleanInput("toJobConfig.appendMode").setValue(true);
        toJobConfig.getEnumInput("toJobConfig.compression").setValue("CUSTOM");
        toJobConfig.getStringInput("toJobConfig.customCompression").setValue("org.apache.parquet.hadoop.codec.SnappyCodec");
        toJobConfig.getStringInput("toJobConfig.outputDirectory").setValue("hdfs://nist23:9000/sqoop116");

        MDriverConfig driverConfig = job.getDriverConfig();
        driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);

        Status status = client.saveJob(job);
        if (status.canProceed()) {
            System.out.println("Created Job with Job Name: " + job.getName());
            return job.getName();
        } else {
            System.out.println("Something went wrong creating the job");
            return null;
        }
    }

    public static String createHdfs2MysqlJob(MLink fromLink, MLink toLink) {
        MJob job = client.createJob(fromLink.getName(), toLink.getName());
        job.setName("xigua-job" + UUID.randomUUID());
        job.setCreationUser("xigua");
        MFromConfig fromJobConfig = job.getFromJobConfig();
        fromJobConfig.getStringInput("fromJobConfig.inputDirectory").setValue("/tmp/sqoop-job/4/");
        MToConfig toJobConfig = job.getToJobConfig();
        toJobConfig.getStringInput("toJobConfig.tableName").setValue("sqoop_jobs");
        toJobConfig.getListInput("toJobConfig.columnList").setValue(Arrays.asList("id", "name", "alerts", "cron"));
        MDriverConfig driverConfig = job.getDriverConfig();
        driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);

        Status status = client.saveJob(job);
        if (status.canProceed()) {
            System.out.println("Created Job with Job Name: " + job.getName());
            return job.getName();
        } else {
            System.out.println("Something went wrong creating the job");
            return null;
        }


    }

    static void startJob(String jobName) {
        //Job start
        MSubmission submission = client.startJob(jobName);
        System.out.println("Job Submission Status : " + submission.getStatus());
        if (submission.getStatus().isRunning() && submission.getProgress() != -1) {
            System.out.println("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
        }

        System.out.println("Hadoop job id :" + submission.getExternalJobId());
        System.out.println("Job link : " + submission.getExternalLink());
        Counters counters = submission.getCounters();
        if (counters != null) {
            System.out.println("Counters:");
            for (CounterGroup group : counters) {
                System.out.print("\t");
                System.out.println(group.getName());
                for (Counter counter : group) {
                    System.out.print("\t\t");
                    System.out.print(counter.getName());
                    System.out.print(": ");
                    System.out.println(counter.getValue());
                }
            }
        }

    }

    /**
     * 压缩，增量
     * @param args
     */
    public static void main(String[] args) {
        //todo 1、参数封装；2、link共用；3、job列表维护

        System.setProperty("HADOOP_USER_NAME", "Administrator");
        // 注意sqoop 后面有个 /,如果没有会报下面的错,非常诡异
        // Exception: org.apache.sqoop.common.SqoopException Message: CLIENT_0004:Unable to find valid Kerberos ticket cache (kinit)
        String url = "http://192.168.9.23:12000/sqoop/";
        client = new SqoopClient(url);
        System.out.println(client);

        MLink mysqlLink = createMysqlLink();
        MLink hdfsLink = createHdfsLink();
//        MLink oracleLink = createOracleLink();
        startJob(createMysql2HdfsAppendJob(mysqlLink, hdfsLink));
        // 先把数据导入 hdfs
//        startJob(createMysql2HdfsJob(oracleLink, hdfsLink));
        // 然后再把数据导回 mysql
        /*startJob(createHdfs2MysqlJob(hdfsLink, mysqlLink));*/
    }
}
