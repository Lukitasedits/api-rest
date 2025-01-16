package com.lukitasedits.api_rest.filters;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.lukitasedits.api_rest.controllers.WebFluxErrorController;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.services.RequestLogService;

import reactor.core.publisher.Mono;

@Component
public class RequestLogFilter implements WebFilter {

    @Autowired
    private RequestLogService requestLogService;

    @Autowired
    private WebFluxErrorController errorController;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/api/percentage")) {
            LocalDateTime requestTime = LocalDateTime.now();
            String endpoint = exchange.getRequest().getURI().toString();
            Map<String, Float> params = new HashMap<>();
            try {
                exchange.getRequest().getQueryParams().forEach((key, value) -> {
                        params.put(key, Float.parseFloat(value.get(0)));
                });
            } catch (NumberFormatException e) {
                return errorController.handleException(exchange, HttpStatus.BAD_REQUEST, "Invalid parameter: " + e.getMessage());
            }
            RequestLog requestLog = new RequestLog(requestTime, endpoint, params);
            requestLogService.openRequest(requestLog);
            
            return chain.filter(exchange).doOnTerminate(() -> {
                requestLogService.closeRequest();
            });
        }

        return chain.filter(exchange);
    }
}
