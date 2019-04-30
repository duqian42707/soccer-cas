package com.dqv5.cas.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CasServerProperties
 */
@Data
@Configuration
@ConfigurationProperties(prefix="custom.cas.server")
public class CasServerProperties {

    private String casServerName;

    private String casServerLoginUrl;
    private boolean useSession = true;
    private boolean redirectAfterValidation = true;

    
}