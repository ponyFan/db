package com.ga.common.sqoop;

import com.ga.common.sqoop.entity.Mysql2HdfsModel;

/**
 * @author zelei.fan
 * @date 2020/6/2 9:37
 * @description
 */
public class SqoopUtils {

    /**
     * sql->hdfs
     * @param model
     * @param url
     * @throws Exception
     */
    public static void sql2hdfs(Mysql2HdfsModel model, String url) throws Exception {
        /*String[] args = new String[] {
                "--connect", url,
                "--username", model.getUser(),
                "--password", model.getPassword(),
                "--table", model.getTable(),
                "-m", model.getMapTask(),
                "--target-dir", model.getTargetDir(),
        };
        System.out.println("************cmd is : " + Arrays.toString(args));
        runSqoop(args, model.getDefaultFS());*/
    }

    /**
     * 执行sqoop任务
     * @param args sqoop参数
     * @param hdfs hdfs集群地址
     * @throws Exception
     */
    public static void runSqoop(String[] args, String hdfs) throws Exception {
        /*String[] expandArguments = OptionsFileUtil.expandArguments(args);
        SqoopTool tool = SqoopTool.getTool("import");
        Configuration conf = new Configuration();
        if (StringUtils.isNotBlank(hdfs)){
            conf.set("fs.default.name", hdfs);//设置HDFS服务地址
        }
        Configuration loadPlugins = SqoopTool.loadPlugins(conf);
        Sqoop sqoop = new Sqoop((com.cloudera.sqoop.tool.SqoopTool) tool, loadPlugins);
        Sqoop.runSqoop(sqoop, expandArguments);*/
    }
}
