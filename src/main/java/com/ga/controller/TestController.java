package com.ga.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ga.annotion.checkparam.Check;
import com.ga.annotion.time.Timing;
import com.ga.common.HbaseTemplate;
import com.ga.common.RestfulTemplate;
import com.ga.common.WebSocketServer;
import com.ga.dao.TicketRowkeyIndexDao;
import com.ga.entity.dto.TicketRowkeyIndexDO;
import com.ga.entity.model.BaseModel;
import com.ga.entity.vo.PersonIdTicketVO;
import com.ga.entity.vo.StudentVO;
import com.ga.service.StudentService;
import com.ga.utils.DateUtil;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Api(value = "测试模块")
public class TestController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private RestfulTemplate restfulTemplate;

    @Autowired
    private TicketRowkeyIndexDao ticketRowkeyIndexDao;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Value("${device.code}")
    private String deviceCode;

    @Value("${device.street}")
    private String deviceStreet;

    @PostMapping("/test")
    @ApiOperation(value = "参数为对象", notes = "无参")
    public String testModel(@Valid StudentVO student){
        checkIp();
        return "hello!!" + student.toString();
    }

    public boolean checkIp(){
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            System.out.println("host ip is : " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/test1")
    @ApiImplicitParam(value = "一条消息", dataType = "String")
    public String testParam(String msg){
        return msg;
    }

    @PostMapping("/insert")
    @ApiOperation("测试插入数据，测试校验非空注解，方法计时注解")
    @Check
    @Timing
    public BaseModel insert(@Valid StudentVO student){
        studentService.insert(student);
        return BaseModel.simpleSuccessModel();
    }

    @PostMapping("/testTransaction")
    @ApiOperation("测试事务")
    public void testTransaction(StudentVO vo){
        studentService.testTransaction(vo);
    }

    @PostMapping("/send")
    @ApiOperation(value = "发测试数据到kafka")
    public void sendMsg(String topic){
        Faker faker = new Faker(Locale.CHINA);
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1; i ++){
            pool.execute(() -> {
                while (true){
                    String msg = faker.address().buildingNumber() + "," +
                            faker.company().name() + "," +
                            faker.company().profession() + "," +
                            faker.address().fullAddress() + "," +
                            faker.address().state() + "," +
                            faker.app().name() + "," +
                            faker.app().version() + "," +
                            System.currentTimeMillis()/1000;
                    System.out.println("send msg : " + msg);
                    kafkaTemplate.send(topic, msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @PostMapping("/testSocket")
    @ApiOperation("test socket")
    public void testSocket(){
        ExecutorService pool = Executors.newFixedThreadPool(20);
        String[] code = new String[]{"01320100058", "01320100056", "01320100012"};
        String[] station = new String[]{"南京长途客运站", "南京南长途客运站", "南京东长途客运站"};
        String msg = "{\"data\":{\"birthday\":\"19871203\",\"stationCode\":\"01320100058\",\"address\":\"南京市鼓楼区新模范马路66号\",\"flag\":0,\"code\":\"8001\",\"nation\":\"汉\",\"endValidDate\":\"20210222\",\"sex\":\"男\",\"userName\":\"张秀良\",\"beginValidDate\":\"20110222\",\"deviceSn\":\"FC242218490010\",\"insertTime\":1586934544007,\"idCardNum\":\"342625198712032438\",\"station\":\"南京长途客运站\",\"ticketSum\":[{\"code\":\"01320100012\",\"num\":0,\"name\":\"南京东长途客运站\"},{\"code\":\"01320100056\",\"num\":0,\"name\":\"南京南长途客运站\"},{\"code\":\"01320100058\",\"num\":60,\"name\":\"南京长途客运站\"}],\"rowKey\":\"1586934543876-342625198712032438\",\"signOrg\":\"南京市公安局鼓楼分局\"},\"type\":\"PERSON_TICKET\"}";
        for (int i = 0; i < 20; i++) {
            pool.execute(() -> {
                while (true){
                    Random random = new Random();
                    int nextInt = random.nextInt(3);
                    JSONObject jsonObject = JSON.parseObject(msg);
                    jsonObject.put("stationCode", code[nextInt]);
                    jsonObject.put("station", station[nextInt]);
                    WebSocketServer.sendMsg(jsonObject.toJSONString());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 直接返回页面
     * @return
     */
    @GetMapping("img")
    public String img(){
        return "test";
    }

    @PostMapping("/testTicket11")
    @ApiOperation("测试接口")
    public void testTicket(){
        ExecutorService pool = Executors.newFixedThreadPool(12);
        String param = "http://221.226.191.197:8081/faceverfiy/verfiy?cert_no=342625198712032438&company_code=01320100058&machine_code=8001&md5key=123456ABCDEF&sign=3ace32b8bfa1e3162cb26db1aeb703a4";
        for (int i = 0; i < 12; i++) {
            while (true){
                long l = System.currentTimeMillis();
                String s = (String) restfulTemplate.call(param, HttpMethod.GET, null, String.class);
                long l1 = System.currentTimeMillis();
                System.out.println(s);
                System.out.println(Thread.currentThread().getName() +" spend time : "+ (l1 - l) +"ms");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/testTicket")
    @ApiOperation("测试接口")
    public void testUrl(@RequestBody JSONObject object, String url){
        String[] sns = {"FN-00011", "FN-00018", "FN-00005", "FN-00014", "FN-00001"};
        ExecutorService pool = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 15; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        Faker faker = new Faker(Locale.CHINA);
                        int i = new Random().nextInt(5);
                        String sn = sns[i];
                        object.put("deviceSn", sn);
                        object.put("address", faker.address().fullAddress());
                        object.put("idCardNum", faker.idNumber().valid());
                        object.put("userName", faker.name().fullName());
                        object.put("birthday", DateUtil.long2String(System.currentTimeMillis(), DateUtil.DATE_TIME));
                        long l = System.currentTimeMillis();
                        restfulTemplate.call(url, HttpMethod.POST, object, JSONObject.class);
                        long l1 = System.currentTimeMillis();
                        System.out.println("sned msg : " + object.getString("userName"));
                        System.out.println("spend time : " + (l1 - l) + "ms");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @PostMapping("/insertTicket")
    @ApiOperation("接口录入数据")
    public void testUrl(@RequestBody PersonIdTicketVO vo){
        String[] sns = {"1001", "1002", "1003"};
        String[] streets = {"01320100012", "01320100058", "01320100056"};
        String[] stations = {"南京长途客运站东站", "南京长途客运站", "南京长途客运站南站"};
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            Faker faker = new Faker(Locale.CHINA);
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<TicketRowkeyIndexDO> list = Lists.newArrayList();
                    ArrayList<Put> puts = Lists.newArrayList();
                    while (true){
                        int i = new Random().nextInt(3);
                        String sn = sns[i];
                        String street = streets[i];
                        String station = stations[i];
                        vo.setDeviceSn(sn);
                        vo.setAddress(faker.address().fullAddress());
                        vo.setIdCardNum(faker.idNumber().valid());
                        vo.setUserName(faker.name().fullName());
                        vo.setBirthday(DateUtil.long2String(System.currentTimeMillis(), DateUtil.DATE_TIME));
                        /*long l = System.currentTimeMillis();
                        restfulTemplate.call(url, HttpMethod.POST, vo, JSONObject.class);
                        long l1 = System.currentTimeMillis();
                        System.out.println("sned msg : " + vo.getUserName());
                        System.out.println("spend time : " + (l1 - l) + "ms");*/
                        String s = "{\n" +
                                "\t\"flag\":\"0\",\n" +
                                "\t\"messages\":\"通过验证\",\n" +
                                "\t\"data\":[\n" +
                                "\t\t{\n" +
                                "\t\t\t\"fcsj\":\"13:00\",\n" +
                                "\t\t\t\"pz\":\"全\",\n" +
                                "\t\t\t\"xm\":\"张三\",\n" +
                                "\t\t\t\"zjh\":\"450900198511110973\",\n" +
                                "\t\t\t\"ddz\":\"盐城\",\n" +
                                "\t\t\t\"scdd\":\"南京客运站\",\n" +
                                "\t\t\t\"bch\":\"kk1334\",\n" +
                                "\t\t\t\"fcrq\":\"20200227\",\n" +
                                "\t\t\t\"pj\":\"94\"\n" +
                                "\t\t}\n" +
                                "\t]\n" +
                                "  }";
                        JSONObject call = JSON.parseObject(s);
                        String flag = call.getString("flag");
                        String data = call.getString("data");
                        long l = System.currentTimeMillis();
                        String rowKey = assembleRowKey(vo, l);
                        insertHbase(rowKey, vo, data, flag, street, sn, station, String.valueOf(l), puts);
                        insertIndex(l, rowKey, sn, street, vo.getIdCardNum(), list);
                    }
                }
            });
        }
    }

    /**
     * 反转时间，组成rowkey
     * @param ticketVO
     * @return
     */
    private String assembleRowKey(PersonIdTicketVO ticketVO, long currentTime){
        String idCardNum = ticketVO.getIdCardNum();
        return currentTime + "-" + idCardNum;
    }

    private void insertIndex(long time, String rowkey, String code, String street, String id, List<TicketRowkeyIndexDO> list){
        TicketRowkeyIndexDO aDo = new TicketRowkeyIndexDO();
        JSONObject codeMap = JSONObject.parseObject(deviceCode);
        JSONObject streetMap = JSONObject.parseObject(deviceStreet);
        aDo.setInsertTime(time);
        aDo.setCode(codeMap.getInteger(code));
        aDo.setStreet(streetMap.getInteger(street));
        aDo.setIdCard(id);
        list.add(aDo);
        if (list.size() % 500 == 0){
            System.out.println("insert mysql..." + DateUtil.getCurrentDateString(DateUtil.DATE_TIME));
            ticketRowkeyIndexDao.insertBatch(list);
            list.clear();
        }
    }

    private void insertHbase(String rowKey, PersonIdTicketVO ticketVO, String data, String flag, String street, String code, String station, String checkTime, List<Put> list){
        String family = "cf";
        String tableName = "ticket4";
        byte[] bytes = family.getBytes();
        try {
            hbaseTemplate.createTable(tableName, family);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Put put = new Put(Bytes.toBytes(rowKey));
        String deviceSn = ticketVO.getDeviceSn();
        put.addColumn(bytes, "deviceSn".getBytes(), null == deviceSn ? new byte[0] : deviceSn.getBytes());
        String userName = ticketVO.getUserName();
        put.addColumn(bytes, "userName".getBytes(), null == userName ? new byte[0] : userName.getBytes());
        String idCardNum = ticketVO.getIdCardNum();
        put.addColumn(bytes, "idCardNum".getBytes(), null == idCardNum ? new byte[0] : idCardNum.getBytes());
        String picBase64 = ticketVO.getPicBase64();
        put.addColumn(bytes, "picBase64".getBytes(), null == picBase64 ? new byte[0] : picBase64.getBytes());
        String address = ticketVO.getAddress();
        put.addColumn(bytes, "address".getBytes(), null == address ? new byte[0] : address.getBytes());
        String sex = ticketVO.getSex();
        put.addColumn(bytes, "sex".getBytes(), null == sex ? new byte[0] : sex.getBytes());
        String signOrg = ticketVO.getSignOrg();
        put.addColumn(bytes, "signOrg".getBytes(), null == signOrg ? new byte[0] : signOrg.getBytes());
        String birthday = ticketVO.getBirthday();
        put.addColumn(bytes, "birthday".getBytes(), null == birthday ? new byte[0] : birthday.getBytes());
        String nation = ticketVO.getNation();
        put.addColumn(bytes, "nation".getBytes(), null == nation ? new byte[0] : nation.getBytes());
        String beginValidDate = ticketVO.getBeginValidDate();
        put.addColumn(bytes, "beginValidDate".getBytes(), null == beginValidDate ? new byte[0] : beginValidDate.getBytes());
        String endValidDate = ticketVO.getEndValidDate();
        put.addColumn(bytes, "endValidDate".getBytes(), null == endValidDate ? new byte[0] : endValidDate.getBytes());
        put.addColumn(bytes, "ticket".getBytes(), null == data ? new byte[0] : data.getBytes());
        JSONArray array = JSONArray.parseArray(data);
        JSONObject jsonObject = array.getJSONObject(0);
        String fcrq = jsonObject.getString("fcrq");
        String fcsj = jsonObject.getString("fcsj");
        String goTime = DateUtil.getString(fcrq, DateUtil.DATE_STRING, DateUtil.DATE) +" "+ fcsj;
        put.addColumn(bytes, "goTime".getBytes(), null == goTime ? new byte[0] : goTime.getBytes());
        String scdd = jsonObject.getString("scdd");
        put.addColumn(bytes, "goStation".getBytes(), null == scdd ? new byte[0] : scdd.getBytes());
        String ddz = jsonObject.getString("ddz");
        put.addColumn(bytes, "targetStation".getBytes(), null == ddz ? new byte[0] : ddz.getBytes());
        String bch = jsonObject.getString("bch");
        put.addColumn(bytes, "shift".getBytes(), null == bch ? new byte[0] : bch.getBytes());
        String pz = jsonObject.getString("pz");
        put.addColumn(bytes, "ticketType".getBytes(), null == pz ? new byte[0] : pz.getBytes());
        put.addColumn(bytes, "flag".getBytes(), null == flag ? new byte[0] : flag.getBytes());
        put.addColumn(bytes, "street".getBytes(), null == street ? new byte[0] : street.getBytes());
        put.addColumn(bytes, "station".getBytes(), null == station ? new byte[0] : station.getBytes());
        put.addColumn(bytes, "code".getBytes(), null == code ? new byte[0] : code.getBytes());
        put.addColumn(bytes, "checkTime".getBytes(), checkTime.getBytes());
        list.add(put);
        if (list.size() % 500 == 0){
            System.out.println("insert hbase..." + DateUtil.getCurrentDateString(DateUtil.DATE_TIME));
            Connection connection = hbaseTemplate.getConnection();
            try {
                Table table = connection.getTable(TableName.valueOf(tableName));
                table.put(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.clear();
        }
    }
}
