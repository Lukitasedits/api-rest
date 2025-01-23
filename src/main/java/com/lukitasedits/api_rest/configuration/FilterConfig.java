package com.lukitasedits.api_rest.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lukitasedits.api_rest.filters.RateLimitFilter;
import com.lukitasedits.api_rest.filters.RequestLogFilter;

@Configuration
public class FilterConfig {
    
    // @Bean
    // public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
    //     FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new RateLimitFilter());
    //     registrationBean.addUrlPatterns("/api/*");
    //     registrationBean.setOrder(1);
    //     return registrationBean;
    // }

    // @Bean
    // public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
    //     FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new RequestLogFilter());
    //     registrationBean.addUrlPatterns("/api/percentage/*");
    //     registrationBean.setOrder(2);
    //     return registrationBean;
    // }
}
