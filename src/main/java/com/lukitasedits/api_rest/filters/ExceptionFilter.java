package com.lukitasedits.api_rest.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.lukitasedits.api_rest.exceptions.BadParamException;
import com.lukitasedits.api_rest.exceptions.EmptyResponseException;
import com.lukitasedits.api_rest.exceptions.TooManyRequestException;
import com.lukitasedits.api_rest.services.RequestLogService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.lukitasedits.api_rest.models.Error;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final RequestLogService requestLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException, TooManyRequestException {
        try {
            filterChain.doFilter(request, response);
        } catch (TooManyRequestException e) {
            handleException(response, e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        } catch (EmptyResponseException e) {
            handleException(response, e.getMessage(), HttpStatus.BAD_GATEWAY);
        } catch (BadParamException e) {
            handleException(response, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException e) {
            handleException(response, e.getMessage(), e.getStatusCode());
        } catch (RuntimeException e) {
            handleException(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(HttpServletResponse response, String message, HttpStatusCode status) throws IOException {
        Error error = Error.builder().error(message).build();
        try {
            if (requestLogService.isRequestOpen()) {
                requestLogService.updateResponse(new ResponseEntity<>(error, status));
                log.info("Closing by exception filter");
                requestLogService.closeRequest();
            }
        } catch (Exception e) {
            requestLogService.cancelRequest();
            handleException(response, "Request error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(error.toString());
        response.getWriter().flush();
    }
}