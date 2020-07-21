package com.ga.common.sercurity;

import lombok.Data;

/**
 * Created by jingwk on 2019/11/17
 */
@Data
public class LoginUser {

    private String username;
    private String password;
    private Integer rememberMe;

}
