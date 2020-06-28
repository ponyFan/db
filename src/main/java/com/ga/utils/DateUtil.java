package com.ga.utils;


import com.ga.common.ResultCode;
import com.ga.common.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zelei.fan
 * @date 2019/12/5 19:58
 */
public class DateUtil {

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_1 = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_STRING = "yyyyMMdd";
    public static final String DATE_TIME_STRING = "yyyyMMddHHmmss";


    /**
     * 获取当前字符串时间
     * @return
     */
    public static String getCurrentDateString(String format){
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date());
    }

    /**
     * 时间戳转string
     * @param timestamp
     * @return
     */
    public static String long2String(long timestamp, String format){
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(timestamp);
    }

    /**
     * String 转时间戳
     * @param time
     * @return
     */
    public static long string2Long(String time, String format){
        if (StringUtils.isBlank(time) || StringUtils.isBlank(format)){
            return 0L;
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            Date date = sf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.TIME_PARES_ERROR, e.getMessage());
        }
    }


    /**
     * 获取字符串日期
     * @param date
     * @return
     */
    public static String getDataString(Date date){
        SimpleDateFormat sf = new SimpleDateFormat(DATE);
        return sf.format(date);
    }

    /**
     * 转换字符串类型
     * @param time
     * @return
     */
    public static String getString(String time, String format){
        if (StringUtils.isBlank(time) || StringUtils.isBlank(format)){
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        SimpleDateFormat sf1 = new SimpleDateFormat(DATE_TIME_STRING);
        Date date = null;
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.TIME_PARES_ERROR, e.getMessage());
        }
        return sf1.format(date);
    }

    /**
     * 转换字符串类型
     * @param time
     * @return
     */
    public static String getString(String time, String sourceFormat, String targetFormat){
        if (StringUtils.isBlank(time) || StringUtils.isBlank(sourceFormat) || StringUtils.isBlank(targetFormat)){
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat(sourceFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(targetFormat);
        Date date = null;
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.TIME_PARES_ERROR, e.getMessage());
        }
        return sf1.format(date);
    }

    public static void getYearDate() throws ParseException {
        String dateStart="2019-01-01";
        String dateEnd="2019-12-31";
        SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd");
        long startTime = date.parse(dateStart).getTime();//start
        long endTime = date.parse(dateEnd).getTime();//end
        long day=1000*60*60*24;
        for(long i=startTime;i<=endTime;i+=day) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(i));
            System.out.println(date.format(new Date(i)) +"  "+ (cal.get(Calendar.DAY_OF_WEEK) -1)
                    +"--"+ cal.get(Calendar.YEAR)+"--"+ (cal.get(Calendar.MONTH)+1)+"--"+ cal.get(Calendar.DATE));
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(long2String(1586254163255L, DATE_TIME));
        System.out.println(long2String(1586424054179L, DATE_TIME));
        System.out.println(long2String(1586424054209L, DATE_TIME));
        System.out.println(long2String(1586415273020L, DATE_TIME));
        System.out.println(long2String(1586413098089L, DATE_TIME));
        System.out.println(long2String(1586322073106L, DATE_TIME));
        System.out.println(long2String(1587038713000L, DATE_TIME));

        System.out.println(string2Long("2020-04-23 00:00:00", DATE_TIME));
        System.out.println(new Date());

    }
}
