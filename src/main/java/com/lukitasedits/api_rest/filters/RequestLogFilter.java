package com.lukitasedits.api_rest.filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lukitasedits.api_rest.exceptions.BadParamException;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.services.RequestLogService;

@Component
@Order(3)
public class RequestLogFilter extends OncePerRequestFilter {

    @Autowired
    private RequestLogService requestLogService;

    private static final String TARGET_PATH = "/api/percentage";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws RuntimeException, IOException, ServletException {

        String path = request.getServletPath();
        if (path != null && path.startsWith(TARGET_PATH)) {
            LocalDateTime requestTime = LocalDateTime.now();
            String endpoint = request.getRequestURL().toString();
            Map<String, Float> params = new HashMap<>();

            try {
                request.getParameterMap().forEach((key, values) -> {
                    if (values.length > 0) {
                        params.put(key, Float.parseFloat(values[0]));
                    }
                });
            } catch (NumberFormatException e) {
                throw new BadParamException("Invalid parameter: " + e.getMessage());
            }
            
            RequestLog requestLog = new RequestLog(requestTime, endpoint, params);
            requestLogService.openRequest(requestLog);

            try {
                filterChain.doFilter(request, response);
            } finally {
                requestLogService.closeRequest();
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
