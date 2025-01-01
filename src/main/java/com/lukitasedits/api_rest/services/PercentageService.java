package com.lukitasedits.api_rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lukitasedits.api_rest.models.Percentage;

@Service
public class PercentageService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.external.service.base-url}")
    private String percentageAPIEndPoint;

    @Value("${spring.external.service.key}")
    private String percentageAPIEndKey;

   public Integer getRandomPercentage() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", percentageAPIEndKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Percentage> response = restTemplate.exchange(percentageAPIEndPoint, HttpMethod.GET, entity, Percentage.class);

        Integer percentageVal = 0;
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            percentageVal = response.getBody().getValue();
        }
        return percentageVal;
    }

    public Float sumAndAddPercentage(Float num1, Float num2, Integer percentage) {
        return (num1 + num2) * (100 + percentage) / 100;
    }
}
