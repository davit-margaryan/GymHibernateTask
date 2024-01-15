package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainingController;
import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkload;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkloadClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;

public class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        trainingController = new TrainingController(trainingService, trainerWorkloadClient);
    }

    @Test
    void testCreateTraining() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        CreateTrainingRequestDto requestDto = new CreateTrainingRequestDto();

        when(trainingService.createTraining(Mockito.any())).thenReturn(trainerWorkload);
        ResponseEntity<Void> responseEntity = trainingController.createTraining(requestDto);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}