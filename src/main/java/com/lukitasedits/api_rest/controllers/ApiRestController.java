package com.lukitasedits.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lukitasedits.api_rest.dto.RequestLogDTO;
import com.lukitasedits.api_rest.entities.RequestLog;
import com.lukitasedits.api_rest.services.PercentageService;
import com.lukitasedits.api_rest.services.RequestLogService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private PercentageService percentageService;

    @Autowired
    private RequestLogService requestLogService;

    @PostMapping("/percentage")
    public ResponseEntity<Float> getPercentage(@RequestParam("num1") Float num1, @RequestParam("num2") Float num2) {
        Integer percentage = percentageService.getPercentage();
        Float value = percentageService.sumAndAddPercentage(num1, num2, percentage);

        ResponseEntity<Float> response = new ResponseEntity<>(value, HttpStatus.OK);
        requestLogService.updateResponse(response);
        return response;
    }

    @GetMapping("/log")
    public ResponseEntity<Page<RequestLogDTO>> getRequestLogs(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return new ResponseEntity<>(requestLogService.getRequestLogs(page, size), HttpStatus.OK);
    }
    
}
