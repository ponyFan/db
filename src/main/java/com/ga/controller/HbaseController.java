package com.ga.controller;

import com.alibaba.fastjson.JSONObject;
import com.ga.common.hbase.HbaseTemplate;
import com.ga.entity.model.PageBean;
import com.ga.entity.vo.StudentVO;
import com.ga.utils.DateUtil;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/hbase")
@Api(value = "habse controller test")
public class HbaseController {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private String family = "cf";

    /**
     * 创建hbase表
     * @param tableName
     */
    @PostMapping("/create")
    @ApiOperation(value = "create hbase table")
    public void createTable(String tableName){
        try {
            hbaseTemplate.createTable(tableName, family);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建hbase分区表
     * @param tableName
     * @param records
     * @param partition
     */
    @PostMapping("/createPartition")
    @ApiOperation(value = "create hbase partition table")
    public void createTablePartition(String tableName, long records, int partition){
        try {
            hbaseTemplate.createTablePartition(tableName, family, records, partition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入单条数据到hbase中
     * @param tableName
     * @param stu
     */
    @PostMapping("/insert")
    @ApiOperation(value = "insert data to hbase")
    public void insertTable(@RequestParam(value = "tableName") String tableName, @RequestBody StudentVO stu){
        JSONObject object = new JSONObject();
        object.forEach((s, o) -> {

        });
        while (true){
            long l = System.currentTimeMillis();
            String reverse = new Random().nextInt(9) + "_" + l + "_" + DigestUtils.md5DigestAsHex(Bytes.toBytes(UUID.randomUUID().getLeastSignificantBits()));
            Put put = new Put(Bytes.toBytes(reverse));
            put.addColumn(family.getBytes(), "time".getBytes(), DateUtil.long2String(l, DateUtil.DATE_TIME).getBytes());
            put.addColumn(family.getBytes(), "age".getBytes(), stu.getAge().getBytes());
            put.addColumn(family.getBytes(), "name".getBytes(), stu.getName().getBytes());
            Connection connection = hbaseTemplate.getConnection();
            try {
                Table table = connection.getTable(TableName.valueOf(tableName));
                table.put(put);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新数据
     * @param tableName
     * @param stu
     */
    @PostMapping("/update")
    @ApiOperation(value = "update data to hbase")
    public void updateTable(@RequestParam(value = "tableName") String tableName, @RequestBody StudentVO stu){
        Put put = new Put(Bytes.toBytes(stu.getId()));
        put.addColumn(family.getBytes(), "age".getBytes(), stu.getAge().getBytes());
        put.addColumn(family.getBytes(), "name".getBytes(), stu.getName().getBytes());
        Connection connection = hbaseTemplate.getConnection();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowkey查询单条数据
     * @param tableName
     * @param rowKey
     * @return
     */
    @GetMapping("/queryByRowKey")
    @ApiOperation(value = "query by rowKey")
    public Map<String, String> getDataByRowKey(String tableName, String rowKey){
        try {
            return hbaseTemplate.getDataByRowKey(tableName, rowKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询某个cell的数据
     * @param tableName
     * @param rowKey
     * @param family
     * @param col
     * @return
     */
    @GetMapping("/queryCell")
    @ApiOperation(value = "get cell data")
    public String getCellData(String tableName, String rowKey, String family, String col){
        try {
            return hbaseTemplate.getCellData(tableName, rowKey, family, col);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询整张表
     * @param tableName
     * @return
     */
    @GetMapping("/queryTable")
    @ApiOperation(value = "get table data")
    public List<Map<String, String>> getAllData(String tableName){
        try {
            return hbaseTemplate.getAllData(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据rowkey删除数据
     * @param tableName
     * @param rowKey
     */
    @DeleteMapping("/deleteByRowKey")
    @ApiOperation(value = "delete data by rowkey")
    public void deleteByRowKey(String tableName, String rowKey){
        try {
            hbaseTemplate.deleteByRowKey(tableName, rowKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除某个单元格数据
     * @param tableName
     * @param rowKey
     * @param family
     * @param cell
     */
    @DeleteMapping("/deleteCell")
    @ApiOperation(value = "delete cell")
    public void deleteCell(String tableName, String rowKey, String family, String cell){
        try {
            hbaseTemplate.deleteCell(tableName, rowKey, family, cell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     * @param tableName
     */
    @DeleteMapping("/deleteTable")
    @ApiOperation(value = "delete table")
    public void deleteTable(String tableName){
        try {
            hbaseTemplate.deleteTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowkey过滤，返回对应rowkey的值
     * @param tableName
     * @param rowKey
     * @return
     * @throws IOException
     */
    @GetMapping("/rowkey")
    @ApiOperation(value = "scan by rowkey")
    public List<Map<String, String>> getByRow(String tableName, String rowKey) throws IOException {
        return hbaseTemplate.getByRow(tableName, rowKey);
    }

    /**
     * 根据family过滤，返回对应family的值
     * @param tableName
     * @param family
     * @return
     * @throws IOException
     */
    @GetMapping("/family")
    @ApiOperation(value = "scan by family")
    public List<Map<String, String>> getByFamily(String tableName, String family) throws IOException {
        return hbaseTemplate.getByFamily(tableName, family);
    }

    /**
     * 根据列过滤，返回对应列名的的值
     * @param tableName
     * @param column
     * @return
     * @throws IOException
     */
    @GetMapping("/qualifier")
    @ApiOperation(value = "scan by qualifier")
    public List<Map<String, String>> getQualifier(String tableName, String column) throws IOException {
        return hbaseTemplate.getQualifier(tableName, column);
    }

    /**
     * 根据值过滤,模糊查询
     * @param tableName
     * @param value
     * @return
     * @throws IOException
     */
    @GetMapping("/value")
    @ApiOperation(value = "scan by value")
    public List<Map<String, String>> getValue(String tableName, String value) throws IOException {
        return hbaseTemplate.getValue(tableName, value);
    }

    /**
     * 根据单列过滤，精确查询，返回与value完全匹配的数据
     * @param tableName
     * @param family
     * @param column
     * @param value
     * @return
     * @throws IOException
     */
    @GetMapping("/single")
    @ApiOperation(value = "scan by single col")
    public List<Map<String, String>> getSingleCol(String tableName, String family, String column, String value) throws IOException {
        return hbaseTemplate.getSingleCol(tableName, family, column, value);
    }

    /**
     * 单列排除过滤，返回除了该列的其他列的值
     * @param tableName
     * @param family
     * @param column
     * @param value
     * @return
     * @throws IOException
     */
    @GetMapping("/singleExclude")
    @ApiOperation(value = "scan by single exclude")
    public List<Map<String, String>> getSingleColExclude(String tableName, String family, String column, String value) throws IOException {
        return hbaseTemplate.getSingleColExclude(tableName, family, column, value);
    }

    /**
     * 根据前缀过滤，根据rowkey前缀查找
     * @param tableName
     * @param prefix
     * @return
     * @throws IOException
     */
    @GetMapping("/prefix")
    @ApiOperation(value = "scan by prefix")
    public List<Map<String, String>> getPrefix(String tableName, String prefix) throws IOException {
        return hbaseTemplate.getRowKeyPrefix(tableName, prefix);
    }

    /**
     * rowkey前缀反转模糊查询
     * @param tableName
     * @param prefix
     * @return
     * @throws IOException
     */
    @GetMapping("/prefixReverse")
    @ApiOperation(value = "rowkey前缀反转模糊查询")
    public List<Map<String, String>> getPrefixReverse(String tableName, String prefix) throws IOException {
        return hbaseTemplate.getRowKeyPrefix(tableName, StringUtils.reverse(prefix));
    }

    @GetMapping("/suffix")
    @ApiOperation(value = "scan by suffix")
    public List<Map<String, String>> getSuffix(String tableName, String suffix) throws IOException {
        return hbaseTemplate.getRowKeySuffix(tableName, suffix);
    }

    @GetMapping("/contains")
    @ApiOperation(value = "scan by contains")
    public List<Map<String, String>> getContains(String tableName, String contains) throws IOException {
        return hbaseTemplate.getRowKeyContains(tableName, contains);
    }

    /**
     * 根据列名的前缀过滤，
     * @param tableName
     * @param colPrefix
     * @return
     * @throws IOException
     */
    @GetMapping("/colPrefix")
    @ApiOperation(value = "scan by colprefix")
    public List<Map<String, String>> getColPrefix(String tableName, String colPrefix) throws IOException {
        return hbaseTemplate.getColPrefix(tableName, colPrefix);
    }

    @PostMapping("/createBatch")
    @ApiOperation(value = "create batch data")
    public void createBatch(String tableName){
        Faker faker = new Faker(Locale.CHINA);
        Connection connection = hbaseTemplate.getConnection();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Put> list = Lists.newArrayList();
        for (int i = 0; i < 10000; i ++){
            Put put = new Put(Bytes.toBytes(faker.address().buildingNumber()));
            put.addColumn(family.getBytes(), "name".getBytes(), Bytes.toBytes(faker.app().name()));
            put.addColumn(family.getBytes(), "address".getBytes(), Bytes.toBytes(faker.address().fullAddress()));
            list.add(put);
        }
        try {
            table.put(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * scan范围的数据
     * @param name
     * @param startRow
     * @param stopRow
     * @return
     */
    @PostMapping("/scan")
    @ApiOperation(value = "scan by startrow endrow")
    public List<Map<String, String>> scanByRowKey(String name, String startRow, String stopRow) throws IOException{
        /*String startRow1 = StringUtils.reverse(startRow);
        String stopRow1 = StringUtils.reverse(stopRow);*/
        return hbaseTemplate.scanByRowKey(name, startRow, stopRow);
    }

    /**
     * 分页查询
     * @param table
     * @param start
     * @param page
     * @param end
     * @param map
     * @return
     * @throws IOException
     */
    @GetMapping("/queryPage")
    @ApiOperation("分页查询")
    public PageBean queryByCondition(String table, String start, int page, int pageSize, String end, Map<String, String> map) throws IOException {
        HashMap<String, String> hashMap = Maps.newHashMap();
        hashMap.put("userName", "张秀良");
        byte[][] bytes = {"station".getBytes(), "userName".getBytes(), "idCardNum".getBytes(), "goTime".getBytes(),
                "goStation".getBytes(), "targetStation".getBytes(), "shift".getBytes(), "ticketType".getBytes(),
                "code".getBytes(), "flag".getBytes(), "checkTime".getBytes()};
        return hbaseTemplate.selectByPage(table, "cf", page, pageSize, start, end, hashMap, bytes);
    }

    @GetMapping("/page")
    @ApiOperation("chaxun")
    public List<Map<String, String>> selectPage(String table) throws IOException {
        return hbaseTemplate.selectPage(table);
    }
}
