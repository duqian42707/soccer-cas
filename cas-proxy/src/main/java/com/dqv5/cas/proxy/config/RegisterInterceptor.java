package com.dqv5.cas.proxy.config;

import com.dqv5.cas.proxy.intercepter.CasValIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * RegisterInterceptor
 */
@Configuration
public class RegisterInterceptor extends WebMvcConfigurerAdapter{

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CasValIntercepter())
            .addPathPatterns("/app/validate", "/app/validate_full");
        super.addInterceptors(registry);
    } 
}