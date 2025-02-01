package com.lukitasedits.api_rest.filters;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lukitasedits.api_rest.services.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class RateLimitFilterTest {
    
    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    public void doFilterInternalTest() throws RuntimeException, IOException, ServletException {
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(rateLimiterService).tryConsume(1L);
        verify(filterChain).doFilter(request, response);
    }


}
