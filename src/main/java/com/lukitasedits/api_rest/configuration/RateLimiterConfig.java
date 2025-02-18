package com.lukitasedits.api_rest.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.lukitasedits.api_rest.services.RateLimiterService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {
	
	public final RateLimiterService rateLimiterService;

	@PostConstruct
	public void initRateLimit() {
		rateLimiterService.initRateLimitBucket(3, 3, Duration.ofMinutes(1));
    }

}
