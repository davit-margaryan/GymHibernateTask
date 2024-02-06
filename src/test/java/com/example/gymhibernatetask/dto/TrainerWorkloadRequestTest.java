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

public class TrainerWorkloadRequestTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private TrainerWorkloadRequest createTrainerWorkloadRequest(String username, String firstName, String lastName, String traineeUsername,
                                                                boolean isActive, Date trainingDate,
                                                                Number trainingDuration, String actionType) {
        return new TrainerWorkloadRequest(username, firstName, lastName, traineeUsername, isActive, trainingDate,
                trainingDuration, actionType);
    }

    @Test
    public void testValidationFailsWhenFieldsAreInvalid() {
        TrainerWorkloadRequest request = createTrainerWorkloadRequest("", "", "",
                "", true, null, null, "");

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);

        assertEquals(7, violations.size(), "Expected seven violations due to invalid fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        TrainerWorkloadRequest request = createTrainerWorkloadRequest("username", "First", "Last",
                "Trainee", true, new Date(), 60, "Action");

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}