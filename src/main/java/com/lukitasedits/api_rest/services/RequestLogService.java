package com.lukitasedits.api_rest.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lukitasedits.api_rest.dto.ErrorDTO;
import com.lukitasedits.api_rest.dto.RequestLogDTO;
import com.lukitasedits.api_rest.entities.RequestLog;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.repositories.RequestLogRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestLogService {

    private final RequestLogRepository requestLogRepository;

    private RequestLog currentRequest;

    public boolean isRequestOpen () {
        log.info("Checking if request is open" + currentRequest);
        return currentRequest != null;
    }

    public Page<RequestLogDTO> getRequestLogs(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestLog> requestLogs = requestLogRepository.getRequestLogs(pageable);

        return requestLogs.map(RequestLogDTO::fromEntity);
    }

    public void openRequest (RequestLog requestLog) {
        log.info("Opening request" + requestLog.toString());
        if (isRequestOpen()) {
            throw new RuntimeException("Another request is open");
        }
        this.currentRequest  = requestLog;
    }

    public void updateResponse (@NonNull ResponseEntity<?> response) {
        log.info("Updating response" + response.toString());
        Object body = response.getBody();
        switch (body) {
            case null -> throw new EmptyResponseException("Response body is null");
            case Float floatBody -> this.currentRequest.setResponse(floatBody);
            case ErrorDTO errorBody -> this.currentRequest.setResponse(errorBody);
            default -> throw new RuntimeException("Invalid response: " + body.getClass().getName());
        }
    }

    @Async
    public void closeRequest() {
        log.info("Closing request" + this.currentRequest.toString());
        this.currentRequest = requestLogRepository.save(this.currentRequest);
        this.cancelRequest();
    }

    public void cancelRequest() {
        this.currentRequest = null;
    }
}
