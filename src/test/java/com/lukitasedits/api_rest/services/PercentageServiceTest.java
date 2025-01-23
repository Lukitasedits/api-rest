package com.lukitasedits.api_rest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.exceptions.ExternalException;
import com.lukitasedits.api_rest.models.Percentage;

@ExtendWith(MockitoExtension.class)
public class PercentageServiceTest {
    
    @InjectMocks
    private PercentageService percentageService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisService redisService;

    @Test
    void testGetRandomPercentage_Success() {
        when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Percentage.class)))
        .thenReturn(new ResponseEntity<Percentage>(new Percentage(23), HttpStatus.OK));
        
        Integer percentage = percentageService.getRandomPercentage();
        assertEquals(23, percentage.intValue());
        verify(redisService).setKey("percentage", 23, 30*60);
    }

    @Test
    void testGetRandomPercentage_ExternalServiceFailure() {
        when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Percentage.class)))
        .thenThrow(new RuntimeException("Test exception"));
        assertThrowsExactly(RuntimeException.class,() -> {
            percentageService.getRandomPercentage();
        });
    }

    @Test
    void testGetRandomPercentage_ExternalServiceEmptyResponse() {
        when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Percentage.class)))
        .thenReturn(new ResponseEntity<>(new Percentage(null), HttpStatus.NO_CONTENT));
        
        assertThrowsExactly(EmptyResponseException.class,() -> {
            percentageService.getRandomPercentage();
        });
    }
    
    @Test
    void testTryCache_Success() throws IOException {
        when(redisService.getKey("percentage")).thenReturn(55);
        Integer percentageFromCache = percentageService.tryCache(new RuntimeException());
        assertEquals(55, percentageFromCache.intValue());
    }

    @Test
    void testTryCache_EmptyResponse() throws IOException {
        when(redisService.getKey("percentage")).thenReturn(null);

        assertThrowsExactly(ExternalException.class,() -> {
            percentageService.tryCache(new RuntimeException());
        });
    }

    @Test
    void testTryCache_Failure() throws IOException {
        when(redisService.getKey("percentage")).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrowsExactly(ExternalException.class,() -> {
            percentageService.tryCache(new RuntimeException());
        });
    }

    @Test
    void testSumAndAddPercentage() {
        Float result = percentageService.sumAndAddPercentage(32.1f, 45.12f, 50);
        //32.1 + 45.12 + 50% = 77.22 + 38.61 = 115.83
        assertEquals(115.83f, result.floatValue(), 0.001);
    }
}
