package com.ga.utils;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zelei.fan
 * @date 2020/3/26 16:21
 * @description
 */
public class FileUtils {

    /**
     * 多线程将完整文件写到一个文件中
     * @param sourceFile 需要分割的文件
     * @param targetFile 分割后的文件
     * @param block 将文件分成的块数
     */
    public static void multiThreadWriteFile(String sourceFile, String targetFile, int block){
        CountDownLatch latch = new CountDownLatch(block);
        ExecutorService pool = Executors.newFixedThreadPool(block);
        File file = new File(sourceFile);
        long length = file.length();
        ArrayList<Long> list = Lists.newArrayList();
        splitAvg(length, block, list);
        System.out.println("文件总大小：" + length + "字节");
        for (int i = 0; i < list.size(); i++) {
            int start = Integer.valueOf(String.valueOf(list.get(i)));
            int end;
            if (i != 0){
                start = start + 1;
            }
            if (i == list.size() - 1){
                end = (int) length;
            }else {
                end = Integer.valueOf(String.valueOf(list.get(i + 1)));
            }
            System.out.println("线程" + i + "下载:" + start + "字节~" + end + "字节");
            pool.execute(new DownLoadThread(i, start, end, sourceFile, targetFile, latch));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            pool.shutdown();
        }
    }

    /**
     * 将数字平均分成avg等分
     * @param length
     * @param avg
     * @param list
     */
    public static void splitAvg(long length, int avg, List<Long> list){
        long split = length / avg;
        splitLength(length, split +1, list);
    }

    /**
     * 将数字按步长分割
     * @param length 总长度
     * @param step 步长
     * @param splits
     */
    public static void splitLength(long length, long step, List<Long> splits){
        if (splits.size() == 0){
            splits.add(0L);
            splitLength(length, step, splits);
        }
        Long aLong = splits.get(splits.size() - 1);
        long next = aLong + step;
        if (next <= length){
            splits.add(next);
            splitLength(length, step, splits);
        }else {
            return;
        }
    }

    /**
     * 普通写文件
     * @param sourceFile
     * @param targetFile
     */
    public static void writeFile(String sourceFile, String targetFile){
        File source = new File(sourceFile);
        File target = new File(targetFile);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        byte[] bytes = new byte[1024];
        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(target));
            int off = 0;
            while ((off = in.read(bytes)) != -1){
                out.write(bytes, 0, off);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 多线程写多个文件
     * @param sourceFile
     * @param targetFile
     * @param block
     */
    public static void multiThreadWriteFiles(String sourceFile, String targetFile, int block){
        CountDownLatch latch = new CountDownLatch(block);
        ExecutorService pool = Executors.newFixedThreadPool(block);
        File file = new File(sourceFile);
        long length = file.length();
        ArrayList<Long> list = Lists.newArrayList();
        splitAvg(length, block, list);
        System.out.println("文件总大小：" + length + "字节");
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            String name = "F:\\test\\zabbix" + i +".tar.gz";
            int start = Integer.valueOf(String.valueOf(list.get(i)));
            int end;
            if (i != 0){
                start = start + 1;
            }
            if (i == list.size() - 1){
                end = (int) length;
            }else {
                end = Integer.valueOf(String.valueOf(list.get(i + 1)));
            }
            /*pool.execute(new DownLoadThread1(sourceFile, targetFile, start, end, latch));*/

            File source = new File(sourceFile);
            File target = new File(name);
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            byte[] bytes = new byte[1024];
            try {
                in = new BufferedInputStream(new FileInputStream(source));
                out = new BufferedOutputStream(new FileOutputStream(target));
                int off = 0;
                int count = 0;
                in.skip(start);
                while ((off = in.read()) != -1){
                    out.write(off);
                    count += off;
                    if (count > (end - start + 1)){
                        index += count;
                        System.out.println(file + " 长度 ：" + index);
                        break;
                    }
                }
                out.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        multiThreadWriteFile("F:\\ff\\zabbix-4.4.4.tar.gz", "F:\\test\\zabbix.tar.gz", 5);
        long endTime = System.currentTimeMillis();
        System.out.println("多线程全部下载结束,共耗时" + (endTime - startTime) + "ms");

        long startTime1 = System.currentTimeMillis();
        writeFile("F:\\ff\\zabbix-4.4.4.tar.gz", "F:\\test\\zabbix1.tar.gz");
        long endTime1 = System.currentTimeMillis();
        System.out.println("普通全部下载结束,共耗时" + (endTime1 - startTime1) + "ms");

        multiThreadWriteFiles("F:\\ff\\zabbix-4.4.tar.gz", "F:\\test\\zabbix11.tar.gz", 5);
    }
}

class DownLoadThread implements Runnable {

    private int i;
    private int startIndex;
    private int endIndex;
    private String sourcePath;
    private String targetPath;
    private CountDownLatch latch;

    public DownLoadThread(int i, int startIndex, int endIndex, String sourcePath, String targetPath, CountDownLatch latch) {
        this.i = i;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.latch = latch;
    }

    @Override
    public void run() {
        File file = new File(sourcePath);
        FileInputStream in = null;
        RandomAccessFile raFile = null;
        FileChannel channel = null;
        FileLock lock = null;
        try {
            in = new FileInputStream(file);
            in.skip(startIndex);
            //给要写的文件加锁
            raFile = new RandomAccessFile(targetPath, "rwd");
            channel = raFile.getChannel();
            /*1、轮询获取文件锁*/
            while(true){
                try {
                    lock = channel.tryLock();
                    break;
                } catch (Exception e) {
                    /*System.out.println("有其他线程正在操作该文件，当前进入的线程为："+i);*/
                }
            }
            /*2、成功获取文件锁*/
            //随机写文件的时候从哪个位置开始写
            raFile.seek(startIndex);
            int len = 0;
            byte[] arr = new byte[1024];
            //获取文件片段长度
            int segLength = endIndex - startIndex + 1;
            while ((len = in.read(arr)) != -1) {
                if (segLength > len) {
                    segLength = segLength - len;
                    raFile.write(arr, 0, len);
                } else {
                    raFile.write(arr, 0, segLength);
                    break;
                }
            }
            System.out.println("线程" + i + "下载完毕");
            //计数值减一
            latch.countDown();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (lock != null) {
                    lock.release();
                }
                if (channel != null) {
                    channel.close();
                }
                if (raFile != null) {
                    raFile.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class DownLoadThread1 implements Runnable {

    private String sourceFile;
    private String targetFile;
    private int startOff;
    private int endOff;
    private CountDownLatch latch;

    public DownLoadThread1(String sourceFile, String targetFile, int startOff, int endOff, CountDownLatch latch) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        this.startOff = startOff;
        this.endOff = endOff;
        this.latch = latch;
    }

    @Override
    public void run() {
        File source = new File(sourceFile);
        File target = new File(targetFile);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        byte[] bytes = new byte[1024];
        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(target));
            int off = 0;
            in.skip(startOff);
            while ((off = in.read()) != -1){
                out.write(off);
                if (target.length() > (endOff - startOff + 1)){
                    break;
                }
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
