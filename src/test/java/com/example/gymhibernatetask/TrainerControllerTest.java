package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainerController;
import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TrainerService;
import com.example.gymhibernatetask.util.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateTrainer() {
        CreateTrainerRequestDto trainerRequestDto = new CreateTrainerRequestDto();

        when(trainerService.createTrainer(trainerRequestDto)).thenReturn(new CreateResponseDto());

        ResponseEntity<CreateResponseDto> response = trainerController.createTrainer(trainerRequestDto);

        verify(trainerService).createTrainer(trainerRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetTrainerProfile() {
        String username = "testUser";
        String password = "testPassword";
        String searchUsername = "searchUser";

        when(trainerService.selectTrainerProfile(username, password, searchUsername)).thenReturn(new TrainerResponseDto());

        ResponseEntity<TrainerResponseDto> response = trainerController.getTrainerProfile(username, password, searchUsername);

        verify(trainerService).selectTrainerProfile(username, password, searchUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainer() {
        String username = "testUser";
        String password = "testPassword";
        UpdateTrainerRequestDto updateRequestDto = new UpdateTrainerRequestDto();

        when(trainerService.updateTrainer(username, password, updateRequestDto)).thenReturn(new TrainerResponseDto());

        ResponseEntity<TrainerResponseDto> response = trainerController.updateTrainer(username, password, updateRequestDto);

        verify(trainerService).updateTrainer(username, password, updateRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTrainerTrainingsList() {
        String trainerUsername = "testUser";
        String password = "testPassword";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String traineeName = "John";
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Cardio");

        List<TrainingDto> trainingsList = List.of();
        when(trainerService.getTrainerTrainingsList(trainerUsername, password, periodFrom, periodTo, traineeName, trainingType))
                .thenReturn(trainingsList);

        ResponseEntity<List<TrainingDto>> response = trainerController.getTrainerTrainingsList(
                trainerUsername, password, periodFrom, periodTo, traineeName, trainingType);

        verify(trainerService).getTrainerTrainingsList(
                trainerUsername, password, periodFrom, periodTo, traineeName, trainingType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeActiveStatus() {
        String username = "testUser";
        String password = "testPassword";
        boolean activeStatus = true;

        ResponseEntity<Void> response = trainerController.changeActiveStatus(username, password, activeStatus);

        verify(trainerService).changeActiveStatus(username, password, activeStatus);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
