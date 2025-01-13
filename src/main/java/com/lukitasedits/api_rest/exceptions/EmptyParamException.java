package com.lukitasedits.api_rest.exceptions;

import lombok.Data;

public class EmptyParamException extends RuntimeException {
    public EmptyParamException(String message) {
        super(message);
    }
}
