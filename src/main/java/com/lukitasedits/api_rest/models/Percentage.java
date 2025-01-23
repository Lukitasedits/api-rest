package com.lukitasedits.api_rest.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Percentage implements Serializable {
    private Integer value;
}
