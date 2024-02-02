package com.example.gymhibernatetask.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResourceAccessException;

import javax.security.auth.login.AccountLockedException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void handleIllegalArgumentException() {
        ResponseEntity<String> result = handler.handleIllegalArgumentException(new IllegalArgumentException("Illegal Argument"));

        assertEquals("Illegal Argument", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void handleMethodArgumentNotValidException() throws NoSuchMethodException {
        String object = "object";
        String name = "name";
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(object, name);

        Method method = this.getClass().getMethod("handleMethodArgumentNotValidException");
        MethodParameter parameter = new MethodParameter(method, -1);
        ResponseEntity<String> result =
                handler.handleMethodArgumentNotValidException(
                        new MethodArgumentNotValidException(parameter, errors));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void handleInvalidInputException() {
        ResponseEntity<String> result = handler.handleInvalidInputException(new InvalidInputException("Invalid Input"));

        assertEquals("Invalid Input", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void handleNotFoundException() {
        ResponseEntity<String> result = handler.handleException(new NotFoundException("Not Found"));

        assertEquals("Not Found", result.getBody());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void handleAuthenticationException() {
        ResponseEntity<String> result = handler.handleAuthenticationException(new AuthenticationException("Unauthorized"));

        assertEquals("Unauthorized", result.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void handleAccountLockedException() throws AccountLockedException {
        ResponseEntity<String> result = handler.handleAccountLockedException(new AccountLockedException("Too Many Requests"));

        assertEquals("Too Many Requests", result.getBody());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
    }

    @Test
    public void handleRuntimeException() {
        ResponseEntity<String> result = handler.handleRuntimeException(new RuntimeException("Internal Server Error"));

        assertEquals("Internal Server Error", result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void handleResourceAccessException() {
        ResponseEntity<String> result = handler.handleResourceAccessException(new ResourceAccessException("Request Timeout"));

        assertEquals("The server timed out while trying to process the request. Please try again.", result.getBody());
        assertEquals(HttpStatus.REQUEST_TIMEOUT, result.getStatusCode());
    }

    @Test
    public void handleTimeoutException() {
        ResponseEntity<String> result = handler.handleTimeoutException(new TimeoutException("Request Timeout"));

        assertEquals("Request Timeout", result.getBody());
        assertEquals(HttpStatus.REQUEST_TIMEOUT, result.getStatusCode());
    }
}