package com.lukitasedits.api_rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.lukitasedits.api_rest.entities.RequestLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class RequestLogDTO {
    private Long id;
    private LocalDateTime requestTime;
    private String endpoint;
    private Map<String, Float> params;
    private String response;

     public static RequestLogDTO fromEntity(RequestLog entity) {
        return new RequestLogDTO(
            entity.getId(),
            entity.getRequestTime(),
            entity.getEndpoint(),
            entity.getParams(),
            entity.getResponseJson()
        );
    }
}
