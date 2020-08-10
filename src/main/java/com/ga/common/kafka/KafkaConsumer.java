package com.ga.common.kafka;

import com.ga.common.hbase.HashRowKeyGenerator;
import com.ga.common.hbase.HbaseTemplate;
import com.ga.dao.StudentDAO;
import com.ga.entity.dto.StudentDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class KafkaConsumer {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private StudentDAO studentDAO;



    private Connection connection;
    private Table table;

    @PostConstruct
    public void init(){
        this.connection = hbaseTemplate.getConnection();
        try {
            this.table = connection.getTable(TableName.valueOf("stu1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@KafkaListener(topics = {"${test.topic}"})*/
    public void consume(ConsumerRecord<String, String> record){
        String value = record.value();
        if (StringUtils.isNotBlank(value)){
            String[] split = StringUtils.split(value, ",");
            Put put = new Put(HashRowKeyGenerator.nextId());
            put.addColumn("cf".getBytes(), "name".getBytes(), Bytes.toBytes(split[7]));
            put.addColumn("cf".getBytes(), "address".getBytes(), Bytes.toBytes(split[3]));
            try {
                table.put(put);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @KafkaListener(topics = {"test112"})
    @Transactional
    public void testTransaction(ConsumerRecord<String, String> record){
        String value = record.value();
        StudentDO aDo = new StudentDO();
        aDo.setName("bb");
        aDo.setAge("25");
        if (StringUtils.isNotBlank(value)){
            studentDAO.delete(aDo.getName());
            StudentDO studentDO = new StudentDO();
            BeanUtils.copyProperties(aDo, studentDO);
            studentDAO.insert(studentDO);
        }
    }
}
