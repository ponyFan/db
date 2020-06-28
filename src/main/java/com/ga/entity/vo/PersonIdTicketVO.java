package com.ga.entity.vo;

import lombok.Data;

/**
 * @author zelei.fan
 * @date 2020/2/21 9:53
 * @description 车站人证票对接app接口参数
 */
@Data
public class PersonIdTicketVO {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 身份证
     */
    private String idCardNum;

    /**
     * 图片（base64编码）
     */
    private String picBase64;

    /**
     * 地址
     */
    private String address;

    /**
     * 性别
     */
    private String sex;

    /**
     * 详细地址
     */
    private String signOrg;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 民族
     */
    private String nation;

    /**
     * 有效日期
     */
    private String beginValidDate;

    private String endValidDate;

    @Override
    public String toString() {
        return "PersonIdTicketVO{" +
                "deviceSn='" + deviceSn + '\'' +
                ", userName='" + userName + '\'' +
                ", idCardNum='" + idCardNum + '\'' +
                '}';
    }
}
