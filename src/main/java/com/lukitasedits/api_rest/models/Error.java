package com.lukitasedits.api_rest.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Error implements Serializable {
    private String message;
}
