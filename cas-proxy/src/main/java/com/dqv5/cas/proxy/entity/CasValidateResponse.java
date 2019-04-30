package com.dqv5.cas.proxy.entity;

import lombok.Data;

/**
 * CasValidateResponse
 */
@Data
public class CasValidateResponse {

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * cas登录的账号
     */
    private String account;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 用户的真实姓名
     */
    private String realName;


    /**
     * Auth Center 返回的数据
     */
    private Object data;
}