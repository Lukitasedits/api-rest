package com.lukitasedits.api_rest.exceptions;

import lombok.Data;

@Data
public class EmptyResponseException extends RuntimeException{
    
    public EmptyResponseException(String message) {
        super(message);
    }
}
