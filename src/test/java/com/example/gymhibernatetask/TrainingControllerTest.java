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
        String username = "testUser";
        String password = "testPassword";
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        doNothing().when(trainingService).createTraining(username, password, requestDto);

        ResponseEntity<Void> response = trainingController.createTraining(username, password, requestDto);

        verify(trainingService).createTraining(username, password, requestDto);

        assert response.getStatusCode() == HttpStatus.CREATED;
    }
}
