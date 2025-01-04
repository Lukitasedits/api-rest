package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.services.PercentageService;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private PercentageService percentageService;

    @GetMapping()
    public ResponseEntity<Float> getPercentage(@RequestParam("num1") Float num1, @RequestParam("num2") Float num2) {
        Integer percentage;
        try {
            percentage = percentageService.getRandomPercentage();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (HttpServerErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (ResourceAccessException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (EmptyResponseException e ) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
        Float ret = percentageService.sumAndAddPercentage(num1, num2, percentage);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
    
}
