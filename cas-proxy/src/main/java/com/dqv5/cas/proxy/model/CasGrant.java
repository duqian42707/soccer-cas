package com.dqv5.cas.proxy.model;

import lombok.Data;

/**
 * 对应的 表应用
 * Created by zyj on 2019/3/7.
 */
@Data
public class CasGrant {

    private Integer id;

    private String name;

    private String referer;

    private Integer is_validate = 0;

    private Integer is_validate_full = 0;

    private String created;
}
