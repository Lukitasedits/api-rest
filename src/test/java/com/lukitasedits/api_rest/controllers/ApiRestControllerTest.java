package com.lukitasedits.api_rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.services.PercentageService;
import com.lukitasedits.api_rest.services.RequestLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(ApiRestController.class)
@SpringBootTest
class ApiRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PercentageService percentageService;

    @Mock
    private RequestLogService requestLogService;

    @Test
    void testGetPercentage() throws Exception {
        when(percentageService.getRandomPercentage()).thenReturn(23);
        when(percentageService.sumAndAddPercentage(10.0f, 20.0f, 23)).thenReturn(37.9f);

        mockMvc.perform(post("/api/percentage")
                .param("num1", "10.0")
                .param("num2", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(37.9f));
    }

    @Test
    void testGetRequestLogs() throws Exception {
        RequestLog log1 = new RequestLog();
        RequestLog log2 = new RequestLog();
        List<RequestLog> logs = Arrays.asList(log1, log2);
        Page<RequestLog> pageLogs = new PageImpl<>(logs);

        Pageable pageable = PageRequest.of(0, 2);
        when(requestLogService.getRequestLogs(0, 2)).thenReturn(pageLogs);

        mockMvc.perform(get("/api/log")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists());
    }
}