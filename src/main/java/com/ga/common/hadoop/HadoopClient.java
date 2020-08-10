package com.ga.common.hadoop;


import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;


/**
 * @author zelei.fan
 * @date 2020/8/7 15:53
 * @description
 */
@Component
public class HadoopClient {

    @Value("${hdfs.url}")
    private String hdfsUrl;

    FileSystem fs = null;

    Configuration conf = null;

    @PostConstruct
    public void init() throws Exception{
        conf = new Configuration();
        fs = FileSystem.get(new URI(hdfsUrl), conf, "hadoop");
    }

    /**
     * 上传文件
     * @param src
     * @param dst
     * @throws Exception
     */
    public void upload(String src, String dst) throws Exception {
        fs.copyFromLocalFile(new Path(src), new Path(dst));
        fs.close();
    }

    /**
     * 下载文件
     * @param src
     * @param dst
     * @throws Exception
     */
    public void download(String src, String dst) throws Exception {
        fs.copyToLocalFile(new Path(src), new Path("dst"));
        fs.close();
    }

    /**
     * 创建目录
     * @param file
     * @throws Exception
     */
    public void mkdir(String file) throws Exception {
        boolean mkdirs = fs.mkdirs(new Path(file));
        System.out.println(mkdirs);
    }

    /**
     * 创建文件
     * @param path
     * @throws Exception
     */
    public void mkFile(String path) throws Exception {
        fs.create(new Path(path));
        fs.close();
    }

    /**
     * 创建文件
     * @param path
     * @param override
     * @throws Exception
     */
    public void mkFile(String path, boolean override) throws Exception {
        fs.create(new Path(path), override);
        fs.close();
    }

    /**
     * 删除目录
     * @param file
     * @throws Exception
     */
    public void delete(String file) throws Exception {
        boolean flag = fs.delete(new Path(file), true);
        System.out.println(flag);
    }

    /**
     * 递归所有文件
     * @throws Exception
     */
    public void listFiles(String path) throws Exception {
        if (StringUtils.isBlank(path)){
            path = "/";
        }
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path), true);
        while(listFiles.hasNext()){
            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println("blocksize: " +fileStatus.getBlockSize());
            System.out.println("owner: " +fileStatus.getOwner());
            System.out.println("Replication: " +fileStatus.getReplication());
            System.out.println("Permission: " +fileStatus.getPermission());
            System.out.println("Name: " +fileStatus.getPath().getName());
            System.out.println("------------------");
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            for(BlockLocation b:blockLocations){
                System.out.println("块起始偏移量: " +b.getOffset());
                System.out.println("块长度:" + b.getLength());
                //块所在的datanode节点
                String[] datanodes = b.getHosts();
                for(String dn:datanodes){
                    System.out.println("datanode:" + dn);
                }
            }
        }
    }

}
