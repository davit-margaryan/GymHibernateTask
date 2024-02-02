package com.example.gymhibernatetask.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotFoundExceptionTest {

    @Test
    public void testGetMessage() {
        String message = "Test Message";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }
}