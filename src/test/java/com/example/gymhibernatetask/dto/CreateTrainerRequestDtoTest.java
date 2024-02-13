package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTrainerRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidationFailsWhenSpecializationIsNull() {
        CreateTrainerRequestDto dto = new CreateTrainerRequestDto("First", "Last", null);

        Set<ConstraintViolation<CreateTrainerRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Expected one violation when specialization is null");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {

        CreateTrainerRequestDto dto = new CreateTrainerRequestDto("First", "Last", new TrainingType());

        Set<ConstraintViolation<CreateTrainerRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}