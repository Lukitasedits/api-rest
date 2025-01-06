package com.lukitasedits.api_rest.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
    private String message;
}
