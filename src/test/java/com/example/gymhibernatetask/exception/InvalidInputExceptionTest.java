package com.example.gymhibernatetask.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidInputExceptionTest {

    @Test
    public void testGetMessage() {
        String message = "Test Message";
        InvalidInputException exception = new InvalidInputException(message);

        assertEquals(message, exception.getMessage());
    }
}