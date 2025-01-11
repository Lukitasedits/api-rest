package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.Error;
import com.lukitasedits.api_rest.services.RequestLogService;


@RestControllerAdvice
public class ControllerAdvice {

    @Autowired
    private RequestLogService requestLogService;

    private ResponseEntity<Error> handleException(String message, HttpStatusCode status) {
        System.out.println("handle exception...");
        Error error = Error.builder().message(message).build();
        ResponseEntity<Error> response = new ResponseEntity<>(error, status);
        try {
            if (requestLogService.isRequestOpen()) {
                requestLogService.updateResponse(response);
                requestLogService.closeRequest();
            }
        } catch (Exception e) {
            error = Error.builder().message(e.getMessage()).build();
            requestLogService.cancelRequest();
            return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        return response;
    }

    @ExceptionHandler(value = EmptyResponseException.class)
    public ResponseEntity<Error> emptyResponseExceptionHandler(EmptyResponseException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = RestClientResponseException.class)
    public ResponseEntity<Error> restClientResponseExceptionHandler(RestClientResponseException e) {
        return handleException(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Error> runtimeExceptionHandler(RuntimeException e) {
        return handleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}