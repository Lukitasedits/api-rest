package com.lukitasedits.api_rest.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lukitasedits.api_rest.interceptors.RequestLogInterceptor;

@Configuration
public class InterceptorsConfig  implements WebMvcConfigurer {
    @Autowired
    private RequestLogInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }
}