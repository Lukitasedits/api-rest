package com.lukitasedits.api_rest.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.lukitasedits.api_rest.ApiRestApplication;
import com.lukitasedits.api_rest.models.Percentage;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.repositories.RequestLogRepository;
import com.lukitasedits.api_rest.services.RateLimiterService;
import com.lukitasedits.api_rest.services.RedisService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = ApiRestApplication.class)
@AutoConfigureMockMvc
@Slf4j
class ApiRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimiterService rateLimiterService;

    static PostgreSQLContainer<?> postgreContainer = new PostgreSQLContainer<>("postgres:15")
            .withUsername("test")
            .withPassword("1234")
            .withDatabaseName("test")
            .withExposedPorts(5432);
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7")).withExposedPorts(6379);


    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreContainer::getUsername);
        registry.add("spring.datasource.password", postgreContainer::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.external.service.base-url", () -> wireMockServer.baseUrl());
    }

    @BeforeAll
    static void beforeAll() {
        postgreContainer.start();
        redis.start();
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get("/api/v1/percentage")
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"value\": 23}")
                        .withStatus(200)));
    }

    @AfterAll
    static void afterAll() {
        postgreContainer.stop();
        redis.stop();
        wireMockServer.stop();
    }

    @MockitoBean
    private RequestLogRepository requestLogRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        log.info("BEFORE EACH");
        rateLimiterService.initRateLimitBucket(3, 3, Duration.ofMinutes(1));
    }

    @Test
    void getPercentageTest() throws Exception {
        assertTrue(true);
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(Percentage.class)))
        .thenReturn(new ResponseEntity<Percentage>(new Percentage(23), HttpStatus.OK));
        
        mockMvc.perform(post("/api/percentage")
                .param("num1", "10.0")
                .param("num2", "20.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("36.9"));
        
    }

    @Test
    void getPercentageRetryTest() throws Exception {
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(Percentage.class)))
        .thenThrow(RuntimeException.class);
        when(redisService.getKey("percentage")).thenReturn(23);
        
        mockMvc.perform(post("/api/percentage")
                .param("num1", "10.0")
                .param("num2", "20.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("36.9"));
        
    }

    @Test
    void getRequestLogsTest() throws Exception {
        RequestLog log1 = new RequestLog();
        RequestLog log2 = new RequestLog();
        List<RequestLog> logs = Arrays.asList(log1, log2);
        Page<RequestLog> pageLogs = new PageImpl<>(logs);

        when(requestLogRepository.getRequestLogs(PageRequest.of(0, 2))).thenReturn(pageLogs);

        mockMvc.perform(get("/api/log")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists());
    }

    @Test
    void rateLimitTest() throws Exception {
        when(restTemplate.getForEntity(Mockito.nullable(String.class), Mockito.eq(Percentage.class)))
        .thenReturn(new ResponseEntity<Percentage>(new Percentage(23), HttpStatus.OK));
        
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/percentage")
                    .param("num1", "10.0")
                    .param("num2", "20.0"))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post("/api/percentage")
                .param("num1", "10.0")
                .param("num2", "20.0"))
                .andExpect(status().isTooManyRequests());
    }
}