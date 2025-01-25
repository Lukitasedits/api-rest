package com.lukitasedits.api_rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.lukitasedits.api_rest.entities.RequestLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestLogDTO {
    private Long id;
    private LocalDateTime requestTime;
    private String endpoint;
    private Map<String, Float> params;
    private Object response;

     public static RequestLogDTO fromEntity(RequestLog entity) {
        return new RequestLogDTO(
            entity.getId(),
            entity.getRequestTime(),
            entity.getEndpoint(),
            entity.getParams(),
            entity.getResponse()
        );
    }
}
