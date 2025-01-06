package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lukitasedits.api_rest.services.PercentageService;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private PercentageService percentageService;

    @GetMapping()
    public ResponseEntity<Float> getPercentage(@RequestParam("num1") Float num1, @RequestParam("num2") Float num2) {
        Integer percentage = percentageService.getRandomPercentage();
        Float ret = percentageService.sumAndAddPercentage(num1, num2, percentage);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
