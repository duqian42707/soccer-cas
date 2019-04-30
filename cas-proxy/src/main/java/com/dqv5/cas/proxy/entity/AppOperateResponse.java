package com.dqv5.cas.proxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppOperateResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppOperateResponse {

    private Integer errcode;

    private String msg;
}