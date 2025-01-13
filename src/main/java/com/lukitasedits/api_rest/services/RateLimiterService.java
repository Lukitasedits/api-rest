package com.lukitasedits.api_rest.services;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.lukitasedits.api_rest.exceptions.TooManyRequestException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class RateLimiterService {

    private Bucket bucket;

    public void initRateLimitBucket(long capacity, long tokens, Duration duration) {
        bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(capacity, Refill.intervally(tokens, duration)))
            .build();
    }

    public boolean tryConsume(Long tokens) {
        if (!bucket.tryConsume(tokens)) {
            throw new TooManyRequestException("Too many request. Wait and try again.");
        }
        return true;
    }
}