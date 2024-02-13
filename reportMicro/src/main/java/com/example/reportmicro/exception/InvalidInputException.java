package com.example.reportmicro.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
