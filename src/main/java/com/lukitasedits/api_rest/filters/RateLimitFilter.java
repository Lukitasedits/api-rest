package com.lukitasedits.api_rest.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lukitasedits.api_rest.exceptions.TooManyRequestException;
import com.lukitasedits.api_rest.services.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws RuntimeException, IOException, ServletException {
        rateLimiterService.tryConsume(1L);
        filterChain.doFilter(request, response);
    }
}
