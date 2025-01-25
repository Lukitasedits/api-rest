package com.lukitasedits.api_rest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;

import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.models.Error;
import org.springframework.http.HttpStatus;
import com.lukitasedits.api_rest.models.RequestLog;
import com.lukitasedits.api_rest.repositories.RequestLogRepository;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class RequestLogServiceTest {
    
    @InjectMocks
    private RequestLogService requestLogService;

    @Mock
    private RequestLogRepository requestLogRepository;

    @Test
    public void getRequestLogsTest() {
        RequestLog log1 = new RequestLog();
        RequestLog log2 = new RequestLog();
        List<RequestLog> logs = Arrays.asList(log1, log2);
        Page<RequestLog> pageLogs = new PageImpl<>(logs);

        Pageable pageable = PageRequest.of(0, 2);
        when(requestLogRepository.getRequestLogs(pageable)).thenReturn(pageLogs);

        Page<RequestLog> result = requestLogService.getRequestLogs(0, 2);

        assertEquals(2, result.getTotalElements());
        assertEquals(logs, result.getContent());
    }

    @Test
    public void openRequestTest() {
        RequestLog log = new RequestLog();
        assertEquals(false, requestLogService.isRequestOpen());
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
    }

    @Test
    public void openRequestTest_Failure() {
        RequestLog log = new RequestLog();
        requestLogService.openRequest(log);
        assertThrowsExactly(RuntimeException.class, () -> {
            requestLogService.openRequest(log);
        });
    }

    @Test
    public void cancelRequestWorkflowTest(){
        RequestLog log = new RequestLog();
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
        requestLogService.cancelRequest();
        assertEquals(false, requestLogService.isRequestOpen());
    }

    @Test
    public void updateValidResponseTest(){
        RequestLog log = new RequestLog();
        ResponseEntity<Float> response = ResponseEntity.ok().body(23.0f);
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
        requestLogService.updateResponse(response);
        assertEquals(log.getResponse(), response.getBody());
        requestLogService.closeRequest();
        assertEquals(false, requestLogService.isRequestOpen());
        verify(requestLogRepository).save(log);
    }

    @Test
    public void updateErrorResponseTest(){
        RequestLog log = new RequestLog();
        ResponseEntity<Error> response = new ResponseEntity<>(Error.builder().error("test").build(), HttpStatus.BAD_REQUEST);
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
        requestLogService.updateResponse(response);
        assertEquals(log.getResponse(), response.getBody());
        requestLogService.closeRequest();
        assertEquals(false, requestLogService.isRequestOpen());
        verify(requestLogRepository).save(log);
    }

    @Test
    public void updateInvalidResponseTest(){
        RequestLog log = new RequestLog();
        ResponseEntity<String> response = ResponseEntity.ok().body("test");
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
        assertThrowsExactly(RuntimeException.class, () -> {
            requestLogService.updateResponse(response);
        });
    }

    @Test
    public void updateEmptyResponseTest(){
        RequestLog log = new RequestLog();
        ResponseEntity<?> response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        requestLogService.openRequest(log);
        assertEquals(true, requestLogService.isRequestOpen());
        assertThrowsExactly(EmptyResponseException.class, () -> {
            requestLogService.updateResponse(response);
        });
    }
}
