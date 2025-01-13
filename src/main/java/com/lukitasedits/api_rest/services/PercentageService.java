package com.lukitasedits.api_rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lukitasedits.api_rest.exceptions.EmptyParamException;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.Percentage;

@Service
public class PercentageService {

    @Value("${spring.external.service.base-url}")
    private String percentageAPIEndPoint;

    @Value("${spring.external.service.key}")
    private String percentageAPIEndKey;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WebClient webClient;

    @SuppressWarnings("null")
    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public Integer getRandomPercentage() {
        try {
            Integer percentageVal = webClient.get()
                    .uri(percentageAPIEndPoint)
                    .header("x-api-key", percentageAPIEndKey)
                    .retrieve()
                    .bodyToMono(Percentage.class)
                    .map(response -> {
                        if (response.getValue() == null) {
                            throw new EmptyResponseException("No data available.");
                        }
                        return response.getValue();
                    })
                    .block();

            redisService.setKey("percentage", percentageVal, 30 * 60);

            return percentageVal;

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error calling percentage API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
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
