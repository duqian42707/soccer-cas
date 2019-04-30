package com.dqv5.cas.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CasProxyProperties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "custom.cas.proxy")
public class CasProxyProperties {

    /**
     * cas 服务器 名称
     */
    private String casServerName;

    /**
     * CAS Proxy 应用名称
     */
    private String proxyServerName;


}