package com.lukitasedits.api_rest.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.lukitasedits.api_rest.services.RateLimiterService;

import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements WebFilter {
    
    @Autowired
    private RateLimiterService rateLimiterService;

   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    try {
        rateLimiterService.tryConsume(1L);
        return chain.filter(exchange);
    } catch (Exception e) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        String errorMessage = "Rate limit exceeded: " + e.getMessage();
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(errorMessage.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
}
