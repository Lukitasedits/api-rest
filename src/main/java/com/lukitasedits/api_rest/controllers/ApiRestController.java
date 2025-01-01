package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukitasedits.api_rest.models.Percentage;
import com.lukitasedits.api_rest.services.PercentageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private PercentageService percentageService;

    @GetMapping(path="/{num1},{num2}")
    public Float getPercentage(@PathVariable("num1") Float num1, @PathVariable("num2") Float num2) {
        Integer percentage = percentageService.getRandomPercentage();
        return percentageService.sumAndAddPercentage(num1, num2, percentage);
    }
    
}
