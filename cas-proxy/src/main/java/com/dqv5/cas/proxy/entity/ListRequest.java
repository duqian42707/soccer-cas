package com.dqv5.cas.proxy.entity;

import lombok.Data;

/**
 * ListRequest
 */
@Data
public class ListRequest {

    private String name;

    private Integer page_num = 1;

    private Integer page_size = 10;
}