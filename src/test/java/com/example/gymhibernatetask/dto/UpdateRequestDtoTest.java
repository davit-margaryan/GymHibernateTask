package com.example.gymhibernatetask.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidationFailsWhenFieldsAreBlank() {
        UpdateRequestDto requestDto = new UpdateRequestDto("", "", "");

        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(requestDto);

        assertEquals(3, violations.size(), "Expected three violations due to three blank fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        UpdateRequestDto requestDto = new UpdateRequestDto("username", "John", "Doe");

        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(requestDto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}