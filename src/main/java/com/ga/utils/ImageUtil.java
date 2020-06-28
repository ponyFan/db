package com.ga.utils;

import com.ga.common.ResultCode;
import com.ga.common.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zelei.fan
 * @date 2019/12/5 15:09
 * @description 图片工具类
 */
public class ImageUtil {

    /**
     * 图片转base64
     * @param filepath
     * @return
     */
    public static String imgToBase64(String filepath){
        byte[] data = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filepath);
            data = new byte[stream.available()];
            stream.read(data);
            stream.close();
        }  catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.FILE_READ_ERROR, e.getMessage());
        } finally {
            try {
                if (null != stream) stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * base64转图片
     * @param base64
     * @return
     */
    public static boolean base64ToImg(String base64, File file){
        byte[] decode = Base64.getDecoder().decode(base64);
        ByteArrayInputStream in = new ByteArrayInputStream(decode);
        byte[] bytes = new byte[1024];
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            int len = 0;
            while ((len = in.read(bytes)) != -1){
                out.write(bytes, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.FILE_READ_ERROR, e.getMessage());
        } finally {
            try {
                if (null != out) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        long start = Long.valueOf(StringUtils.reverse("8812525185851"));
        long end = Long.valueOf(StringUtils.reverse("8829445185851"));
        System.out.println(DateUtil.long2String(start, DateUtil.DATE_TIME));
        System.out.println(DateUtil.long2String(end, DateUtil.DATE_TIME));
        System.out.println(DateUtil.long2String(1586256659744L, DateUtil.DATE_TIME));
        System.out.println(DateUtil.long2String(1582156659712L, DateUtil.DATE_TIME));
        System.out.println(DateUtil.long2String(1586459192833L, DateUtil.DATE_TIME));
    }
}
