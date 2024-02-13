package com.example.gymhibernatetask.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateTraineeRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private CreateTraineeRequestDto createTraineeRequestDto(String firstName, String lastName, Date birthDate, String address) {
        return new CreateTraineeRequestDto(firstName, lastName, birthDate, address);
    }

    @Test
    public void testValidationFailsWhenFieldsAreInvalid() {
        CreateTraineeRequestDto dto = createTraineeRequestDto("", "", null, "");

        Set<ConstraintViolation<CreateTraineeRequestDto>> violations = validator.validate(dto);

        assertEquals(4, violations.size(), "Expected four violations due to four invalid fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        CreateTraineeRequestDto dto = createTraineeRequestDto("First", "Last", new Date(), "Address");

        Set<ConstraintViolation<CreateTraineeRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}