package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingDtoTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidationFailsWithInvalidFields() {
        TrainingDto trainingDto = new TrainingDto("", null, null, null, "");
        Set<ConstraintViolation<TrainingDto>> violations = validator.validate(trainingDto);
        assertEquals(5, violations.size(), "Expected five violations due to invalid fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        TrainingDto trainingDto = new TrainingDto("Training", new Date(), new TrainingType(), 60, "Trainer");
        Set<ConstraintViolation<TrainingDto>> violations = validator.validate(trainingDto);
        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }

}