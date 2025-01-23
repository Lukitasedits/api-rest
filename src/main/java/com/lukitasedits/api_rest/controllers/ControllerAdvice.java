package com.lukitasedits.api_rest.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.lukitasedits.api_rest.models.Error;
import com.lukitasedits.api_rest.services.RequestLogService;
import com.lukitasedits.api_rest.exceptions.ExternalException;

@RestControllerAdvice
public class ControllerAdvice {
    @Autowired
    private RequestLogService requestLogService;

    private ResponseEntity<Error> handleException(String message, HttpStatusCode status) {
        Error error = Error.builder().error(message).build();
        ResponseEntity<Error> response = new ResponseEntity<>(error, status);
        try {
            if (requestLogService.isRequestOpen()) {
                requestLogService.updateResponse(response);
                requestLogService.closeRequest();
            }
        } catch (Exception e) {
            requestLogService.cancelRequest();
            error = Error.builder().error(e.getMessage()).build();
            return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        return response;
    }

    @ExceptionHandler(value = ExternalException.class)
    public ResponseEntity<Error> externalExceptionHandler(ExternalException e) {
        return handleException(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Error> handleMissingParams(MissingServletRequestParameterException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Error> handleNotFound(NoHandlerFoundException e) {
        return handleException(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}