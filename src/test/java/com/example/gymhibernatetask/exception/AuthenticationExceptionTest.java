package com.example.gymhibernatetask.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationExceptionTest {

    @Test
    public void testGetMessage() {
        String message = "Test Message";
        AuthenticationException exception = new AuthenticationException(message);

        assertEquals(message, exception.getMessage());
    }
}