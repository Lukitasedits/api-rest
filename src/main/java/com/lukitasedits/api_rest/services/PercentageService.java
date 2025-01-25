package com.lukitasedits.api_rest.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.lukitasedits.api_rest.dto.PercentageDTO;
import com.lukitasedits.api_rest.exceptions.EmptyParamException;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.exceptions.ExternalException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@EnableRetry
@Slf4j
@RequiredArgsConstructor
public class PercentageService {

    @Value("${spring.external.service.base-url}")
    private String percentageAPIEndPoint;

    private final RedisService redisService;
    
    private final RestTemplate restTemplate;

    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public Integer getRandomPercentage() {
        try {
            log.info("Calling external service: " + percentageAPIEndPoint);
            ResponseEntity<PercentageDTO> response = restTemplate.getForEntity(percentageAPIEndPoint, PercentageDTO.class);
            Integer percentageVal = 0;
            if (response.hasBody() && response.getBody().getValue() != null) {
                percentageVal = response.getBody().getValue();
                log.info("External service responded: " + percentageVal);
                redisService.setKey("percentage", percentageVal, 30*60);
            } else {
                throw new EmptyResponseException("No data available.");
            }
            return percentageVal;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Error calling percentage API: " + e.getMessage(), e);
        } catch (EmptyResponseException e) {
            throw new EmptyResponseException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Recover
    public Integer tryCache(RuntimeException e) throws IOException{
        try {
            Integer percentageVal = (Integer) redisService.getKey("percentage");
            if (percentageVal == null) {
                throw new EmptyResponseException("No data available");
            }
            return percentageVal;
        } catch (EmptyResponseException ex) {
            throw new ExternalException(ex.getMessage(), HttpStatus.BAD_GATEWAY);
        } catch (Exception ex) {
            throw new ExternalException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Float sumAndAddPercentage(@NonNull Float num1, @NonNull Float num2, @NonNull Integer percentage) {
        return (num1 + num2) * (100 + percentage) / 100;
    }
}
