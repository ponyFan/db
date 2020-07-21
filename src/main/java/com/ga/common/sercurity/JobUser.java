package com.ga.common.sercurity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zelei.fan
 * @date 2020/7/9 10:01
 * @description
 */
@TableName("job_user")
@Data
public class JobUser {

    private int id;

    private String username;

    private String password;

    private Integer role;

    private Integer groupId;

}
