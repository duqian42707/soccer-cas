package com.dqv5.cas.proxy.constant;

/**
 * ErrCode
 */
public enum ErrCode {

    SUCCESS(2000),
    REDIRECT(2001),
    AUTH_ERROR(2002),
    APP_NO_ACCESS(2003),
    PARAM_ERROR(4001),
    UNKONW_ERROR(5001);

    private Integer errcode;
    
    ErrCode(Integer errcode) {
        this.errcode = errcode;
    }

    public Integer getErrCode() {
       return this.errcode; 
    }
    
}