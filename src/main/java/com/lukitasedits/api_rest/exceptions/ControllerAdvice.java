package com.lukitasedits.api_rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.lukitasedits.api_rest.dto.ErrorDTO;
import com.lukitasedits.api_rest.services.RequestLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {
    
    private final RequestLogService requestLogService;

    private ResponseEntity<ErrorDTO> handleException(String message, HttpStatusCode status) {
        ErrorDTO error = ErrorDTO.builder().error(message).build();
        ResponseEntity<ErrorDTO> response = new ResponseEntity<>(error, status);
        try {
            if (requestLogService.isRequestOpen()) {
                requestLogService.updateResponse(response);
            }
        } catch (Exception e) {
            requestLogService.cancelRequest();
            error = ErrorDTO.builder().error(e.getMessage()).build();
            return new ResponseEntity<ErrorDTO>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        return response;
    }

    @ExceptionHandler(value = ExternalException.class)
    public ResponseEntity<ErrorDTO> handleExternalException(ExternalException e) {
        return handleException(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDTO> handleMissingParams(MissingServletRequestParameterException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(NoHandlerFoundException e) {
        return handleException(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return handleException(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }
}