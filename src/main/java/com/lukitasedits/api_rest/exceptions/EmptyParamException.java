package com.lukitasedits.api_rest.exceptions;

import lombok.Data;

@Data
public class EmptyParamException extends RuntimeException {
    private String message;
    
    public EmptyParamException(String message) {
        super(message);
    }
}
