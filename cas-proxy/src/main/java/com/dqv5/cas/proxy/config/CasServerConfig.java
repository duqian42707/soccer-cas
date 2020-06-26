package com.dqv5.cas.proxy.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CasServerConfig
 */
@Configuration
public class CasServerConfig {

    private static boolean casEnabled  = true;

    /**
     * CAS认证 拦截路径
     */
    private static String CAS_SERVER_AUTH_PATH = "/app/redirect";

    @Autowired
    private CasServerProperties casServerProperties;

    @Autowired
    private CasProxyProperties casProxyProperties;

    @Bean
    public FilterRegistrationBean filterSingleRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SingleSignOutFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        Map<String,String> initParameters = new HashMap<>(1);
        initParameters.put("casServerUrlPrefix", casServerProperties.getCasServerName() + casServerProperties.getCasServerLoginUrl());
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistrationBean() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new AuthenticationFilter());
        Map<String, String> initParameters = new HashMap<>(2);
        initParameters.put("casServerLoginUrl", casServerProperties.getCasServerName() +casServerProperties.getCasServerLoginUrl());
        initParameters.put("serverName", casProxyProperties.getProxyServerName());
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(2);
        List<String> urlPatterns = new ArrayList<>();
        // 设置匹配的url
        urlPatterns.add("/app/redirect");
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }

    @Bean
    public FilterRegistrationBean ValidationFilterRegistrationBean(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        Map<String, String> initParameters = new HashMap<>(2);
        initParameters.put("casServerUrlPrefix", casServerProperties.getCasServerName());
        initParameters.put("serverName", casProxyProperties.getProxyServerName());
        initParameters.put("redirectAfterValidation", "true");
        initParameters.put("useSession", "true");
        initParameters.put("authn_method", "mfa-duo");
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(1);
        List<String> urlPatterns = new ArrayList<>();
        // 设置匹配的url
        urlPatterns.add("/app/redirect");
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }

    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new HttpServletRequestWrapperFilter());
        authenticationFilter.setOrder(3);
        List<String> urlPatterns = new ArrayList<>();
        // 设置匹配的url
        urlPatterns.add("/app/redirect");
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }

}
