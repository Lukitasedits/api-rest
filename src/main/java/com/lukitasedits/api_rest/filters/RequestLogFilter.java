package com.lukitasedits.api_rest.filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lukitasedits.api_rest.entities.RequestLog;
import com.lukitasedits.api_rest.exceptions.BadParamException;
import com.lukitasedits.api_rest.services.RequestLogService;

@Component
@Slf4j
@Order(3)
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {

    private final RequestLogService requestLogService;

    private static final String TARGET_PATH = "/api/percentage";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws RuntimeException, IOException, ServletException {
        log.info("Processing request by RequestLog filter");
        String path = request.getPathInfo() != null ? request.getPathInfo() : request.getServletPath();
        log.info("path is" + path);
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
            } finally {
                RequestLog requestLog = new RequestLog(requestTime, endpoint, params);
                requestLogService.openRequest(requestLog);
            }
            

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
