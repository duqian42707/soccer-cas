package com.dqv5.cas.proxy.entity;

import java.util.List;

import com.dqv5.cas.proxy.model.CasGrant;

import lombok.Data;

/**
 * CasGrantResponse
 */
@Data
public class CasGrantResponse {

    private int total;

    private List<CasGrant> rows;
}