package com.lukitasedits.api_rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lukitasedits.api_rest.exceptions.EmptyParamException;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.Percentage;

@Service
public class PercentageService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.external.service.base-url}")
    private String percentageAPIEndPoint;

    @Value("${spring.external.service.key}")
    private String percentageAPIEndKey;

    @Autowired
    private RedisService redisService;

    @SuppressWarnings("null")
    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public Integer getRandomPercentage() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", percentageAPIEndKey);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Percentage> response = restTemplate.exchange(percentageAPIEndPoint, HttpMethod.GET, entity, Percentage.class);
        
        Integer percentageVal = 0;
        if (response.hasBody() && response.getBody().getValue() != null) {
            percentageVal = response.getBody().getValue();
            redisService.setKey("percentage", percentageVal, 30*60);
        } else {
            throw new EmptyResponseException("No data available.");
        }
        return percentageVal;
    }

    @Recover
    public Integer tryCache(Exception e){
        Integer percentageVal = (Integer) redisService.getKey("percentage");
        if (percentageVal == null) {
            throw new EmptyResponseException("No data available.");
        }
        return percentageVal;
    }

    public Float sumAndAddPercentage(Float num1, Float num2, Integer percentage) {
        if (num1 == null || num2 == null || percentage == null) {
            throw new EmptyParamException("One or more params are empty.");
        } 
        return (num1 + num2) * (100 + percentage) / 100;
    }
}
