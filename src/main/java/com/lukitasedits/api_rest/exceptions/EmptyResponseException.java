package com.lukitasedits.api_rest.exceptions;

public class EmptyResponseException extends RuntimeException{
    
    public EmptyResponseException(String message) {
        super(message);
    }
}
