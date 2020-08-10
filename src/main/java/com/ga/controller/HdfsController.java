package com.ga.controller;

import com.ga.common.hadoop.HadoopClient;
import com.ga.entity.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zelei.fan
 * @date 2020/8/7 16:57
 * @description
 */
@RequestMapping("/hdfs")
@RestController
@Api
public class HdfsController {

    @Autowired
    private HadoopClient hadoopClient;

    @PostMapping("/upload")
    @ApiOperation("")
    public BaseResponse upload(String src, String dst){
        try {
            hadoopClient.upload(src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }

    @PostMapping("/download")
    @ApiOperation("")
    public BaseResponse download(String src, String dst){
        try {
            hadoopClient.download(src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }

    @PostMapping("/delete")
    @ApiOperation("")
    public BaseResponse delete(String file){
        try {
            hadoopClient.delete(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }

    @PostMapping("/mkdir")
    @ApiOperation("")
    public BaseResponse mkdir(String dir){
        try {
            hadoopClient.mkdir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }

    @PostMapping("/mkFile")
    @ApiOperation("")
    public BaseResponse createFile(String file){
        try {
            hadoopClient.mkFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }

    @PostMapping("/list")
    @ApiOperation("")
    public BaseResponse listFile(String path){
        try {
            hadoopClient.listFiles(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.simpleSuccessModel();
    }
}
