package com.example.gymhibernatetask.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private CreateRequestDto createRequestDto(String firstName, String lastName) {
        return new CreateRequestDto(firstName, lastName);
    }

    @Test
    public void fieldsCannotBeBlank() {
        CreateRequestDto dto = createRequestDto("", "");

        Set<ConstraintViolation<CreateRequestDto>> violations = validator.validate(dto);

        assertEquals(2, violations.size(), "Expected two violation since both fields are blank");
    }

    @Test
    public void validDtoDoesNotCauseViolations() {
        CreateRequestDto dto = createRequestDto("First", "Last");

        Set<ConstraintViolation<CreateRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with a valid DTO");
    }
}