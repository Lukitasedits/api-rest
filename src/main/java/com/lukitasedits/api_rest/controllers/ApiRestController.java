package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping()
    public Float getPercentage(@RequestParam("num1") Float num1, @RequestParam("num2") Float num2) {
        Integer percentage = percentageService.getRandomPercentage();
        return percentageService.sumAndAddPercentage(num1, num2, percentage);
    }
    
}
