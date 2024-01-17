package com.example.gymhibernatetask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import javax.security.auth.login.AccountLockedException;
import java.util.concurrent.TimeoutException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleException(Exception exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException exc) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exc.getMessage());
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<String> handleAccountLockedException(AccountLockedException exc) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exc.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("The server timed out while trying to process the request. Please try again.");
    }

    @ExceptionHandler({TimeoutException.class})
    public ResponseEntity<String> handleTimeoutException(TimeoutException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(e.getMessage());
    }

}