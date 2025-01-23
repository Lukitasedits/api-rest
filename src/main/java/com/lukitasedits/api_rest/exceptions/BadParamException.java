package com.lukitasedits.api_rest.exceptions;

public class BadParamException  extends RuntimeException {
    public BadParamException(String message) {
        super(message);
    }
}
