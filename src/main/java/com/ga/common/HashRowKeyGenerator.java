package com.ga.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author zelei.fan
 * @date 2019/11/25 9:03
 * @description 预分区，获取splitkey
 */
public class HashRowKeyGenerator {

    public static byte[] nextId(){
        return Bytes.toBytes(System.currentTimeMillis() / 1000 + StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
    }

    /**
     * 获取分区范围
     * @param records 总数据量
     * @param partition 分区数
     * @return
     */
    public byte[][] createSplitKeys(long records, int partition){
        //创建rowkeys，并存到有序的树结构中
        byte[][] splitKeys = new byte[partition - 1][];
        TreeSet<byte[]> rows = new TreeSet<>(Bytes.BYTES_COMPARATOR);
        for (int i = 0; i < records; i ++){
            rows.add(nextId());
        }
        //取随机key范围，遍历排好序的rowkey，按照分区数，截取相应的位置的rowkey值
        Iterator<byte[]> iterator = rows.iterator();
        int index = 0;
        int len = 0;
        long splitRecord = records / partition;
        while (iterator.hasNext()){
            byte[] next = iterator.next();
            if ((index != 0) && (index % splitRecord == 0)){
                if (len < partition - 1){
                    splitKeys[len] = next;
                    len ++;
                }
            }
            iterator.remove();
            index ++;
        }
        rows.clear();
        return splitKeys;
    }

    public static void main(String[] args) {
        HashRowKeyGenerator generator = new HashRowKeyGenerator();
        byte[][] splitKeys = generator.createSplitKeys(100, 10);
        for (byte[] splitKey : splitKeys) {
            System.out.println(splitKey);
        }
    }
}
