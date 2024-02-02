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

public class UpdateTraineeRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private UpdateTraineeRequestDto createUpdateTraineeRequestDto(String username, String firstName,
                                                                  String lastName, Date dateOfBirth,
                                                                  String address, boolean isActive) {
        return new UpdateTraineeRequestDto(username, firstName, lastName, dateOfBirth, address, isActive);
    }

    @Test
    public void testValidationFailsWithInvalidFields() {
        UpdateTraineeRequestDto dto = createUpdateTraineeRequestDto("", "", "", null, "", true);

        Set<ConstraintViolation<UpdateTraineeRequestDto>> violations = validator.validate(dto);

        assertEquals(5, violations.size(), "Expected five violations due to invalid fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        UpdateTraineeRequestDto dto = createUpdateTraineeRequestDto("username", "First", "Last",
                new Date(), "Address", true);

        Set<ConstraintViolation<UpdateTraineeRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}