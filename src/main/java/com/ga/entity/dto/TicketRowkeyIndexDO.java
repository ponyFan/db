package com.ga.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zelei.fan
 * @date 2020/4/7 11:18
 * @description
 */
@Data
@TableName("ticket_rowkey_index_copy3")
public class TicketRowkeyIndexDO {

    private long insertTime;

    private Integer code;

    private Integer street;

    private String idCard;
}
