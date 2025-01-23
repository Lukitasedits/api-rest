package com.lukitasedits.api_rest.exceptions;

public class EmptyParamException extends RuntimeException {
    public EmptyParamException(String message) {
        super(message);
    }
}
