package com.cfl.shortlink.admin.dto.req;


import lombok.Data;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginReqDTO {
    /**
     * 用户名 密码
     */
    private String username;
    private String password;
}
