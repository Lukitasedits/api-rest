package com.lukitasedits.api_rest.services;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Serializable> redisTemplate;

    public void setKey(String key, Serializable value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setKey(String key, Serializable value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public Serializable getKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}