package com.lukitasedits.api_rest.interceptors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.services.RequestLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLogInterceptor implements HandlerInterceptor{
    
    @Autowired
    private RequestLogService requestLogService;


    //TODO: ver que pasa si salta error
    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("pre Handle...");
        HandlerInterceptor.super.preHandle(request, response, handler);
        
        LocalDateTime requestTime = LocalDateTime.now();
        String endpoint = request.getRequestURL().toString();
        Map<String, Float> params = new HashMap<String,Float>();
        request.getParameterMap().forEach((key, value) -> {
            params.put(key, Float.parseFloat(value[0]));
        });
        RequestLog requestLog = new RequestLog(requestTime, endpoint, params);
        requestLogService.openRequest(requestLog);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        System.out.println("Post handle....");
        requestLogService.closeRequest();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
 
}
