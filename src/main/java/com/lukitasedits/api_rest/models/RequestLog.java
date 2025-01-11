package com.lukitasedits.api_rest.models;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "request_logs")
@NoArgsConstructor
public class RequestLog {

    public RequestLog(LocalDateTime requestTime, String endpoint, Map<String, Float> params){
        this.requestTime = requestTime;
        this.endpoint = endpoint;
        this.params = params;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column()
    private String endpoint;

    @ElementCollection
    @CollectionTable(name = "params", joinColumns = @JoinColumn(name = "request_id"))
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, Float> params;


    @Column(columnDefinition = "TEXT")
    private String responseJson;

    @Transient
    private Object response;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public Object getResponse() {
        if (response == null && responseJson != null) {
            try {
                response = Float.parseFloat(responseJson);
            } catch (NumberFormatException nfe) {
                try {
                    System.out.println("responseJson" + responseJson);
                    response = objectMapper.readValue(responseJson, new TypeReference<Error>() {});
                } catch (IOException e) {
                    throw new RuntimeException("Error deserializing the response", e);
                }
            }
        }
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
        try {
            this.responseJson = objectMapper.writeValueAsString(response);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing the response", e);
        }
    }
}
