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

public class UpdateTrainerRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidationFailsWhenSpecializationIsNull() {
        UpdateTrainerRequestDto dto = new UpdateTrainerRequestDto("username", "firstName", "lastName", null, true);
        Set<ConstraintViolation<UpdateTrainerRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Expected one violation for null specialization");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        TrainingType validSpecialization = new TrainingType();

        UpdateTrainerRequestDto dto = new UpdateTrainerRequestDto("username", "Firstname", "LastName", validSpecialization, true);
        Set<ConstraintViolation<UpdateTrainerRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}