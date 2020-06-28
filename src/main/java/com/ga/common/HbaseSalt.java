package com.ga.common;

import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author zelei.fan
 * @date 2020/3/10 15:32
 * @description hbase创建预分区，加盐避免热点问题，通过协处理器进行查询
 */
public class HbaseSalt {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private static String tableName = "testSplit";
    private static byte[] FAMILY = Bytes.toBytes("cf");
    private static byte[] NAME = Bytes.toBytes("name");
    private static byte[] AGE = Bytes.toBytes("age");

    /**
     * 随机生成A~F中任意一个字母
     * @return
     */
    private static char generateLetter(){
        return (char) (Math.random() * 6 + 'A');
    }

    /**
     * 预分区
     * @return
     */
    private static byte[][] initSplitKeys(){
        byte[][] split = new byte[27][];
        String[] array = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < 6; i ++){
            split[i] = Bytes.toBytes(array[i]);
        }
        return split;
    }

    /**
     * 创建分区表
     */
    private void createSplitTable() {
        String[] cols = {"name", "age"};
        try {
            hbaseTemplate.createTable(tableName, cols, initSplitKeys());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入数据
     */
    private void insertBatch(){
        createSplitTable();
        BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
        mutatorParams.writeBufferSize(1024 * 1024 * 24);
        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "192.168.9.22,192.168.9.23,192.168.9.24");
        Connection connection = null;
        BufferedMutator bufferedMutator = null;
        try {
            connection = ConnectionFactory.createConnection(conf);
            bufferedMutator = connection.getBufferedMutator(mutatorParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Put> putList = Lists.newArrayList();
        Faker faker = new Faker(Locale.CHINA);
        int index = 0;
        for (int i = 0; i < 1000000; i ++){
            Put put = new Put(Bytes.toBytes(generateLetter() + "-" + UUID.randomUUID().getLeastSignificantBits()));
            put.addColumn(FAMILY, NAME, Bytes.toBytes(faker.name().fullName()));
            put.addColumn(FAMILY, AGE, Bytes.toBytes(faker.number().numberBetween(0, 99)));
            putList.add(put);
            index ++;
            if (index % 1000 == 0){
                try {
                    bufferedMutator.mutate(putList);
                    bufferedMutator.flush();
                    putList.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}
