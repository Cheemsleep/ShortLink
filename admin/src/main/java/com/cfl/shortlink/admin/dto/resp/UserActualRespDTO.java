package com.cfl.shortlink.admin.dto.resp;

import com.cfl.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 用户返回参数响应 无脱敏
 */
@Data
public class UserActualRespDTO {
    private Long id;
    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;


}
