package com.lukitasedits.api_rest.exceptions;

import org.springframework.http.HttpStatusCode;

import lombok.Data;

@Data
public class ExternalException extends RuntimeException{
    private HttpStatusCode statusCode;

    public ExternalException (String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
