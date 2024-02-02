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

public class CreateTrainingRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private CreateTrainingRequestDto createTrainingRequestDto(String traineeUsername, String trainerUsername,
                                                              String trainingName, Date date, Number duration) {
        CreateTrainingRequestDto requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername(traineeUsername);
        requestDto.setTrainerUsername(trainerUsername);
        requestDto.setTrainingName(trainingName);
        requestDto.setDate(date);
        requestDto.setDuration(duration);
        return requestDto;
    }

    @Test
    public void testValidationFailsWhenFieldsAreInvalid() {
        CreateTrainingRequestDto dto = createTrainingRequestDto("", "", "", null, null);

        Set<ConstraintViolation<CreateTrainingRequestDto>> violations = validator.validate(dto);

        assertEquals(5, violations.size(), "Expected five violations due to invalid fields");
    }

    @Test
    public void testValidationSucceedsWithValidFields() {
        CreateTrainingRequestDto dto = createTrainingRequestDto("Trainee", "Trainer",
                "Training", new Date(), 60);

        Set<ConstraintViolation<CreateTrainingRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations with valid fields");
    }
}