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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.lukitasedits.api_rest.dto.PercentageDTO;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.exceptions.ExternalException;
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PercentageServiceTest {
    
    @InjectMocks
    private PercentageService percentageService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisService redisService;

    @Test
    void getPercentageTest_Success() {
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(PercentageDTO.class)))
        .thenReturn(new ResponseEntity<PercentageDTO>(new PercentageDTO(23), HttpStatus.OK));
        
        Integer percentage = percentageService.getPercentage();
        assertEquals(23, percentage.intValue());
        verify(redisService).setKey("percentage", 23, 30*60);
    }

    @Test
    void getPercentageTest_ExternalServiceFailure() {
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(PercentageDTO.class)))
        .thenThrow(new RuntimeException("Test exception"));
        assertThrowsExactly(RuntimeException.class,() -> {
            percentageService.getPercentage();
        });
    }

    @Test
    void getPercentageTest_ExternalServiceEmptyResponse() {
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(PercentageDTO.class)))
        .thenReturn(new ResponseEntity<>(new PercentageDTO(null), HttpStatus.NO_CONTENT));
        
        assertThrowsExactly(EmptyResponseException.class,() -> {
            percentageService.getPercentage();
        });
    }
    
    @Test
    void tryCacheTest_Success() throws IOException {
        when(redisService.getKey("percentage")).thenReturn(55);
        Integer percentageFromCache = percentageService.tryCache(new RuntimeException());
        assertEquals(55, percentageFromCache.intValue());
    }

    @Test
    void tryCacheTest_EmptyResponse() throws IOException {
        when(redisService.getKey("percentage")).thenReturn(null);

        assertThrowsExactly(ExternalException.class,() -> {
            percentageService.tryCache(new RuntimeException());
        });
    }

    @Test
    void tryCacheTest_Failure() throws IOException {
        when(redisService.getKey("percentage")).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrowsExactly(ExternalException.class,() -> {
            percentageService.tryCache(new RuntimeException());
        });
    }

    @Test
    void sumAndAddPercentageTest_Success() {
        Float result = percentageService.sumAndAddPercentage(32.1f, 45.12f, 50);
        //32.1 + 45.12 + 50% = 77.22 + 38.61 = 115.83
        assertEquals(115.83f, result.floatValue(), 0.001);
    }

    @Test
    void sumAndAddPercentageTest_Failure() {

        assertThrowsExactly(NullPointerException.class,() -> {
            percentageService.sumAndAddPercentage(null, 45.12f, 50);
        });

        assertThrowsExactly(NullPointerException.class,() -> {
            percentageService.sumAndAddPercentage(32.1f, null, 50);
        });

        assertThrowsExactly(NullPointerException.class,() -> {
            percentageService.sumAndAddPercentage(32.1f, 45.12f, null);
        });
    }
}
