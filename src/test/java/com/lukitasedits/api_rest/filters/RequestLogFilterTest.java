package com.lukitasedits.api_rest.filters;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.lukitasedits.api_rest.exceptions.BadParamException;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.services.RequestLogService;

@ExtendWith(MockitoExtension.class)
public class RequestLogFilterTest {

    @Mock
    private RequestLogService requestLogService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RequestLogFilter requestLogFilter;

    final String PATH = "/api/percentage";

    @Autowired
    private Environment environment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    
    @Test
    public void testDoFilterInternal_withValidParams() throws IOException, ServletException {
        
        when(request.getServletPath()).thenReturn("/api/percentage");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://lukitasedits.com/api/percentage"));
        when(request.getParameterMap()).thenReturn(Map.of("param1", new String[]{"1.0"}));

        requestLogFilter.doFilterInternal(request, response, filterChain);

        verify(requestLogService).openRequest(any());
        verify(filterChain).doFilter(request, response);
        verify(requestLogService).closeRequest();
    }

    @Test
    public void testDoFilterInternal_withInvalidParams() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/percentage");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://lukitasedits.com/api/percentage"));
        when(request.getParameterMap()).thenReturn(Map.of("param1", new String[]{"invalid value"}));

        assertThrowsExactly(BadParamException.class, () -> {
            requestLogFilter.doFilterInternal(request, response, filterChain);
        });

        verify(requestLogService, never()).openRequest(any(RequestLog.class));
        verify(filterChain, never()).doFilter(request, response);
        verify(requestLogService, never()).closeRequest();
    }

    @Test
    public void testDoFilterInternal_withNonTargetPath() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/other");

        requestLogFilter.doFilterInternal(request, response, filterChain);

        verify(requestLogService, never()).openRequest(any());
        verify(requestLogService, never()).closeRequest();
        verify(filterChain).doFilter(request, response);
    }
}