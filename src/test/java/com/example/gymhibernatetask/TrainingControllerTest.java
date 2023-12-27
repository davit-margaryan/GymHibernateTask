package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainingController;
import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateTraining() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        doNothing().when(trainingService).createTraining(requestDto);

        ResponseEntity<Void> response = trainingController.createTraining(requestDto);

        verify(trainingService).createTraining(requestDto);

        assert response.getStatusCode() == HttpStatus.CREATED;
    }
}
