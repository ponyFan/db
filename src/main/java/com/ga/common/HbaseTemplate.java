package com.ga.common;

import com.ga.entity.model.PageBean;
import com.ga.enums.HbaseFilterEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zelei.fan
 * @date 2019/11/21 09:45
 */
@Component
public class HbaseTemplate {

    @Value("${hbase.quorum}")
    private String quorum;

    @Value("${hbase.master}")
    private String master;

    @Value("${hbase.zookeeper.port}")
    private String zkPort;

    private Connection connection;

    private static final String ROW_KEY = "rowKey";

    @PostConstruct
    public void initConfig() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", zkPort);
        configuration.set("hbase.zookeeper.quorum", quorum);
        configuration.set("hbase.master", master);
        this.connection = ConnectionFactory.createConnection(configuration);
    }

    public Connection getConnection(){
        return this.connection;
    }

    /**
     * 创建表
     * @param tableName 表名
     * @param cols 列族
     * @return
     */
    public int createTable(String tableName, String[] cols, byte[][] splitKeys) throws IOException {
        TableName table = TableName.valueOf(tableName);
        Admin admin = connection.getAdmin();
        if (admin.tableExists(table)){
            return NumberUtils.BYTE_ONE;
        }else {
            TableDescriptorBuilder tdc = TableDescriptorBuilder.newBuilder(table);
            for (String col : cols) {
                ColumnFamilyDescriptorBuilder cdb = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(col));
                ColumnFamilyDescriptor familyDescriptor = cdb.build();
                tdc.setColumnFamily(familyDescriptor);
            }
            if (null != splitKeys){
                admin.createTable(tdc.build(), splitKeys);
            }else {
                admin.createTable(tdc.build());
            }
            return NumberUtils.BYTE_ONE;
        }
    }

    /**
     * 创建表，单个列族
     * @param tableName
     * @param col
     * @return
     */
    public int createTable(String tableName, String col) throws IOException {
        return createTable(tableName, new String[]{col}, null);
    }

    /**
     * 创建分区表
     * @param tableName
     * @param col
     * @param records 总数据量（预计）
     * @param partition 分区数
     * @return
     * @throws IOException
     */
    public int createTablePartition(String tableName, String col, long records, int partition) throws IOException {
        HashRowKeyGenerator generator = new HashRowKeyGenerator();
        byte[][] splitKeys = generator.createSplitKeys(records, partition);
        return createTable(tableName, new String[]{col}, splitKeys);
    }

    /**
     * 根据rowkey获取某条记录
     * @param tableName
     * @param rowKey
     * @return
     */
    public Map<String, String> getDataByRowKey(String tableName, String rowKey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        HashMap<String, String> map = Maps.newHashMap();
        map.put(ROW_KEY, rowKey);
        if (!get.isCheckExistenceOnly()){
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                map.put(colName, value);
            }
        }
        return map;
    }

    /**
     * 查询指定cell内容
     * @param tableName
     * @param rowKey
     * @param family
     * @param col
     * @return
     */
    public String getCellData(String tableName, String rowKey, String family, String col) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        if (!get.isCheckExistenceOnly()){
            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(col));
            Result result = table.get(get);
            byte[] resByte = result.getValue(Bytes.toBytes(family), Bytes.toBytes(col));
            return Bytes.toString(resByte);
        }
        return null;
    }

    /**
     * 获取表中所有信息
     * @param tableName
     * @return
     */
    public List<Map<String, String>> getAllData(String tableName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        return assembleResult(table, new Scan());
    }

    /**
     * 根据rowkey删除
     * @param tableName
     * @param rowKey
     */
    public void deleteByRowKey(String tableName, String rowKey) throws IOException {
        deleteCell(tableName, rowKey, null, null);
    }

    /**
     * 删除某一列
     * @param tableName
     * @param rowKey
     * @param family
     * @param cell
     */
    public void deleteCell(String tableName, String rowKey, String family, String cell) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        if (null != family && null != cell){
            delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(cell));
        }
        table.delete(delete);
    }

    /**
     * 删除表
     * @param tableName
     * @throws IOException
     */
    public void deleteTable(String tableName) throws IOException {
        TableName name = TableName.valueOf(tableName);
        Admin admin = connection.getAdmin();
        admin.disableTable(name);
        admin.deleteTable(name);
    }

    /**
     * 根据rowkey过滤，返回对应rowkey的值
     * @param tableName
     * @param rowKey
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getByRow(String tableName, String rowKey) throws IOException {
        return filter(tableName, rowKey, null, null, null, HbaseFilterEnum.ROW);
    }

    /**
     * 根据family过滤，返回对应family的值
     * @param tableName
     * @param family
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getByFamily(String tableName, String family) throws IOException {
        return filter(tableName, null, family, null, null, HbaseFilterEnum.FAMILY);
    }

    /**
     * 根据列过滤，返回对应列名的的值
     * @param tableName
     * @param column
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getQualifier(String tableName, String column) throws IOException {
        return filter(tableName, null, null, column, null, HbaseFilterEnum.QUALIFIER);
    }

    /**
     * 根据值过滤,模糊查询
     * @param tableName
     * @param value
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getValue(String tableName, String value) throws IOException {
        return filter(tableName, null, null, null, value, HbaseFilterEnum.VALUE);
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
    public List<Map<String, String>> getSingleCol(String tableName, String family, String column, String value) throws IOException {
        return filter(tableName, null, family, column, value, HbaseFilterEnum.SINGLE_COL);
    }

    /**
     * 单利排除过滤，返回一条或多条除了该列的其他列的值
     * @param tableName
     * @param family
     * @param column
     * @param value
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getSingleColExclude(String tableName, String family, String column, String value) throws IOException {
        return filter(tableName, null, family, column, value, HbaseFilterEnum.SINGLE_COL_EXCLUDE);
    }

    /**
     * 根据前缀过滤，根据rowkey前缀查找
     * @param tableName
     * @param prefix
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getRowKeyPrefix(String tableName, String prefix) throws IOException {
        return filter(tableName, prefix, null, null, null, HbaseFilterEnum.ROW_PREFIX);
    }

    /**
     * 根据rowkey后缀过滤
     * @param tableName
     * @param suffix
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getRowKeySuffix(String tableName, String suffix) throws IOException {
        return filter(tableName, suffix, null, null, null, HbaseFilterEnum.ROW_SUFFIX);
    }

    /**
     * 根据rowkey包含的字符过滤
     * @param tableName
     * @param contains
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getRowKeyContains(String tableName, String contains) throws IOException {
        return filter(tableName, contains, null, null, null, HbaseFilterEnum.ROW_CONTAINS);
    }

    /**
     * 根据列前缀过滤,根据列名前缀查找
     * @param tableName
     * @param colPrefix
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getColPrefix(String tableName, String colPrefix) throws IOException {
        return filter(tableName, null, null, colPrefix, null, HbaseFilterEnum.COLUMN_PREFIX);
    }

    /**
     * 过滤
     * @param tableName
     * @param rowKey
     * @param family
     * @param column
     * @param value
     * @param type
     * @return
     */
    private List<Map<String, String>> filter(String tableName, String rowKey, String family, String column, String value,
                                             HbaseFilterEnum type) throws IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.setReversed(true);
        switch (type){
            case ROW:
                RowFilter rowFilter = new RowFilter(CompareOperator.EQUAL, new BinaryComparator(rowKey.getBytes()));
                scan.setFilter(rowFilter);
                break;
            case FAMILY:
                FamilyFilter familyFilter = new FamilyFilter(CompareOperator.EQUAL, new BinaryComparator(family.getBytes()));
                scan.setFilter(familyFilter);
                break;
            case QUALIFIER:
                QualifierFilter qualifierFilter = new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(column.getBytes()));
                scan.setFilter(qualifierFilter);
                break;
            case VALUE:
                ValueFilter valueFilter = new ValueFilter(CompareOperator.EQUAL, new SubstringComparator(value));
                scan.setFilter(valueFilter);
                break;
            case SINGLE_COL:
                SingleColumnValueFilter singleFilter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(column), CompareOperator.EQUAL, Bytes.toBytes(value));
                scan.setFilter(singleFilter);
                break;
            case SINGLE_COL_EXCLUDE:
                SingleColumnValueExcludeFilter excludeFilter = new SingleColumnValueExcludeFilter(Bytes.toBytes(family), Bytes.toBytes(column), CompareOperator.EQUAL, Bytes.toBytes(value));
                scan.setFilter(excludeFilter);
                break;
            case ROW_PREFIX:
                PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(rowKey));
                scan.setFilter(prefixFilter);
                break;
            case ROW_SUFFIX:
                String suffix = ".*" + rowKey + "$";
                RowFilter suffixFilter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator(suffix));
                scan.setFilter(suffixFilter);
                break;
            case ROW_CONTAINS:
                RowFilter containsFilter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator(rowKey));
                scan.setFilter(containsFilter);
                break;
            case COLUMN_PREFIX:
                ColumnPrefixFilter columnPrefixFilter = new ColumnPrefixFilter(column.getBytes());
                scan.setFilter(columnPrefixFilter);
                break;
        }
        return assembleResult(table, scan);
    }

    /**
     * scan范围的数据,rowkey可以时模糊查询
     * @param name
     * @param startRow
     * @param stopRow
     * @return
     */
    public List<Map<String, String>> scanByRowKey(String name, String startRow, String stopRow) throws IOException {
        Table table = connection.getTable(TableName.valueOf(name));
        Scan scan = new Scan();
        /*在startrow加上#和endrow加上：能保证闭区间*/
        scan.withStartRow(Bytes.toBytes(startRow+"#"));
        scan.withStopRow(Bytes.toBytes(stopRow+":"));
        /*RegexStringComparator comparator = new RegexStringComparator(startRow);
        RegexStringComparator comparator1 = new RegexStringComparator(stopRow);
        RowFilter rowFilter = new RowFilter(CompareOperator.GREATER_OR_EQUAL, comparator);
        scan.setFilter(rowFilter);
        RowFilter rowFilter1 = new RowFilter(CompareOperator.LESS_OR_EQUAL, comparator1);
        scan.setFilter(rowFilter1);*/
        return assembleResult(table, scan);
    }

    /**
     * 根据列条件查询
     * @param tableName
     * @param cf
     * @param queryMap
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> queryByColValueCondition(String tableName, String cf, Map<String, String> queryMap) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        FilterList filterList = addFilterCondition(queryMap, cf);
        scan.setFilter(filterList);
        return assembleResult(table, scan);
    }

    /**
     * 增加过滤条件
     * @param queryMap
     * @param cf
     */
    private FilterList addFilterCondition(Map<String, String> queryMap, String cf){
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotBlank(value)){
                SingleColumnValueFilter valueFilter = new SingleColumnValueFilter(Bytes.toBytes(cf), Bytes.toBytes(key), CompareOperator.EQUAL, Bytes.toBytes(value));
                filterList.addFilter(valueFilter);
            }
        }
        return filterList;
    }

    /**
     * 组装返回结果
     * @param table
     * @param scan
     * @return
     * @throws IOException
     */
    private List<Map<String, String>> assembleResult(Table table, Scan scan) throws IOException {
        ResultScanner scanner = table.getScanner(scan);
        List<Map<String, String>> list = Lists.newArrayList();
        for (Result result : scanner) {
            String row = Bytes.toString(result.getRow());
            Cell[] cells = result.rawCells();
            HashMap<String, String> map = Maps.newHashMap();
            map.put(ROW_KEY, row);
            for (Cell cell : cells) {
                String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                String value1 = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                map.put(colName, value1);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 分页查询
     * 分页思路:1、在满足查询条件情况下，获取到list
     *         2、根据页码和size去除list中对应的列
     * @param tableName
     * @param cf
     * @param pageSize 每页条数
     * @param start rowkey起始时间
     * @param end rowkey结束时间
     * @param queryMap 列的查询条件
     * @param cols 需要返回的列
     * @return
     */
    public PageBean selectByPage(String tableName, String cf, int page, int pageSize, String start, String end, Map<String, String> queryMap, byte[][] cols) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        /*由于rowkey按ascaii值的顺序排序，ascii码默认是升序，如果需要倒叙的话需要将endrow设置成起始行，startrow设置成终止行*/
        scan.withStartRow(Bytes.toBytes(end + ":"));
        scan.withStopRow(Bytes.toBytes(start + "#"));
        /*过滤条件*/
        FilterList filterList = addFilterCondition(queryMap, cf);
        /*查询指定列*/
        MultipleColumnPrefixFilter prefixFilter = new MultipleColumnPrefixFilter(cols);
        filterList.addFilter(prefixFilter);
        scan.setFilter(filterList);
        List<Map<String, String>> list = Lists.newArrayList();
        scan.setReversed(true);
        ResultScanner scanner = table.getScanner(scan);
        int startIndex = (page - 1) * pageSize;
        int endIndex = startIndex + pageSize;
        int index = 0;
        for (Result result : scanner) {
            if ( startIndex <= index && index < endIndex){
                String row = Bytes.toString(result.getRow());
                Cell[] cells = result.rawCells();
                HashMap<String, String> map = Maps.newHashMap();
                map.put(ROW_KEY, row);
                for (Cell cell : cells) {
                    String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                    String value1 = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    map.put(colName, value1);
                }
                list.add(map);
            }
            index ++;
        }
        PageBean pageBean = new PageBean();
        pageBean.setCount(index);
        pageBean.setPageSize(pageSize);
        pageBean.setPage(page);
        pageBean.setData(list);
        return pageBean;
    }

    public List<Map<String, String>> selectPage(String tableName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        /*查询指定列*/
        byte[][] bytes = {"station".getBytes(), "userName".getBytes(), "idCardNum".getBytes(), "goTime".getBytes(),
                "goStation".getBytes(), "targetStation".getBytes(), "shift".getBytes(), "ticketType".getBytes(),
                "code".getBytes(), "flag".getBytes(), "checkTime".getBytes()};
        MultipleColumnPrefixFilter prefixFilter = new MultipleColumnPrefixFilter(bytes);
        scan.setFilter(prefixFilter);
        return assembleResult(table, scan);
    }
}
