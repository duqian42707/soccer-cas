package com.dqv5.cas.server.service;

import com.alibaba.fastjson.JSONObject;

/**
 * MonitorService
 */
public interface MonitorService {

    /**
     * 获取cas中的 session信息
     */
    JSONObject obternSessions();
}