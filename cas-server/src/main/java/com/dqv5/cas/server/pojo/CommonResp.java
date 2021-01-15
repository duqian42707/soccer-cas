package com.dqv5.cas.server.pojo;

import lombok.Data;

/**
 * @author duqian
 * @date 2019-06-04
 */
@Data
public class CommonResp {
    private boolean success;
    private String message;
    private Object data;

    public CommonResp() {
    }

    public CommonResp(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CommonResp(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
