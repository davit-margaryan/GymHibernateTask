package com.example.gymhibernatetask.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
