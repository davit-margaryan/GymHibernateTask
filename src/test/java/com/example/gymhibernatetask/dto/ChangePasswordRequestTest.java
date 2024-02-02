package com.example.gymhibernatetask.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChangePasswordRequestTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidationFailsWithBlankFields() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("");
        request.setOldPassword("");
        request.setNewPassword("");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Expected validation to fail with blank fields");
        assertEquals(3, violations.size(), "Expected 3 violations due to 3 blank fields");
    }

    @Test
    public void testValidationSucceedWithValidFields() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("username");
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected validation to pass with valid fields");
    }
}