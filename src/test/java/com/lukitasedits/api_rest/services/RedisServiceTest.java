package com.lukitasedits.api_rest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.junit.jupiter.api.Test;


@ExtendWith(MockitoExtension.class)
public class RedisServiceTest {
    
    @InjectMocks
    private RedisService redisService;

    @Mock
    private RedisTemplate<String, Serializable> redisTemplate;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValueOperations<String, Serializable> valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void setKeyTest() {
        redisService.setKey("key", "value");
        verify(redisTemplate.opsForValue()).set("key", "value");

        redisService.setKey("key", "value", 30);
        verify((redisTemplate).opsForValue()).set("key", "value", 30, TimeUnit.SECONDS);
    }

    @Test
    public void getKeyTest() {
        when(redisTemplate.opsForValue().get("key")).thenReturn(25.2f);
        Float value = (Float)redisService.getKey("key");
        assertEquals(25.2, value, 0.0001);
    }

}
