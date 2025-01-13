package com.lukitasedits.api_rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.models.Error;
import com.lukitasedits.api_rest.repositories.RequestLogRepository;

@Service
public class RequestLogService {

    @Autowired
    private RequestLogRepository requestLogRepository;

    private RequestLog currentRequest;

    public boolean isRequestOpen () {
        return currentRequest != null;
    }

    public Page<RequestLog> getRequestLogs(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return requestLogRepository.getRequestLogs(pageable);
    }

    public void openRequest (RequestLog requestLog) {
        if (isRequestOpen()) {
            throw new RuntimeException("Another request is opened");
        }
        this.currentRequest  = requestLog;
    }

    public void updateResponse (ResponseEntity<?> response) {
        Object body = response.getBody();
        switch (body) {
            case null -> throw new EmptyResponseException("Response body is null");
            case Float floatBody -> this.currentRequest.setResponse(floatBody);
            case Error errorBody -> this.currentRequest.setResponse(errorBody);
            default -> throw new RuntimeException("Invalid response: " + body.getClass().getName());
        }
    }

    @Async
    public void closeRequest() {
        requestLogRepository.save(this.currentRequest);
        this.cancelRequest();
    }

    public void cancelRequest() {
        this.currentRequest = null;
    }
}
