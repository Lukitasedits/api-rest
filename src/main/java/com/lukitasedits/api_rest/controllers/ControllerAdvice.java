package com.lukitasedits.api_rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.Error;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = EmptyResponseException.class)
    public ResponseEntity<Error> emptyResponseExceptionHandler(EmptyResponseException e) {
        Error error  = Error.builder().message(e.getMessage()).build();
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }  

    @ExceptionHandler(value = RestClientResponseException.class)
    public ResponseEntity<Error> restClientResponseExceptionHandler(RestClientResponseException e) {
        Error error  = Error.builder().message(e.getMessage()).build();
        return new ResponseEntity<>(error, e.getStatusCode());
    }  

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Error> runtimeExceptionHandler(RuntimeException e) {
        Error error  = Error.builder().message(e.getMessage()).build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
