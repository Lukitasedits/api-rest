package com.lukitasedits.api_rest.models;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.validator.constraints.EAN;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "request_logs")
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column()
    private String endpoint;

    @Column()
    private Map<String, Object> params;

    @Column()
    private ResponseEntity<Float> response;

    @Column()
    private Error error;
}
