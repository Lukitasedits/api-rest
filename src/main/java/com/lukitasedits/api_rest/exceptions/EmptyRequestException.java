package com.lukitasedits.api_rest.exceptions;

public class EmptyRequestException extends RuntimeException{
    
    public EmptyRequestException(String message) {
        super(message);
    }
}
