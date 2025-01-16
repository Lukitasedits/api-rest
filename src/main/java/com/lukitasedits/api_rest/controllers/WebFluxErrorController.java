package com.lukitasedits.api_rest.controllers;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.lukitasedits.api_rest.models.Error;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class WebFluxErrorController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Void> handleException(ServerWebExchange exchange, HttpStatus status, String errorMessage) {
        try {
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            Error error = Error.builder()
                .message(errorMessage)
                .build();

            byte[] bytes = objectMapper.writeValueAsBytes(error);

            DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }
}
