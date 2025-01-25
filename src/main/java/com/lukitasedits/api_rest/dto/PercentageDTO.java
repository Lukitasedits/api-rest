package com.lukitasedits.api_rest.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PercentageDTO implements Serializable {
    private Integer value;
}
