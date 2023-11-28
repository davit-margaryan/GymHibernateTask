package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TraineeController;
import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.util.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraineeControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateTrainee() {
        CreateTraineeRequestDto traineeRequestDto = new CreateTraineeRequestDto();

        when(traineeService.createTrainee(traineeRequestDto)).thenReturn(new CreateResponseDto());

        ResponseEntity<CreateResponseDto> response = traineeController.createTrainee(traineeRequestDto);

        verify(traineeService).createTrainee(traineeRequestDto);

        assert response.getStatusCode() == HttpStatus.valueOf(201);
    }

    @Test
    void testDeleteTrainee() {
        String username = "testUser";
        String password = "testPassword";
        String deleteUsername = "deleteUser";

        ResponseEntity<Void> response = traineeController.deleteTrainee(username, password, deleteUsername);

        verify(traineeService).deleteTrainee(username, password, deleteUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(204);
    }

    @Test
    void testGetTraineeProfile() {
        String username = "testUser";
        String password = "testPassword";
        String searchUsername = "searchUser";

        when(traineeService.selectTraineeProfile(username, password, searchUsername)).thenReturn(new TraineeResponseDto());

        ResponseEntity<TraineeResponseDto> response = traineeController.getTraineeProfile(username, password, searchUsername);

        verify(traineeService).selectTraineeProfile(username, password, searchUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testUpdateTrainee() {
        String username = "testUser";
        String password = "testPassword";
        UpdateTraineeRequestDto updateRequestDto = new UpdateTraineeRequestDto();

        when(traineeService.updateTrainee(username, password, updateRequestDto)).thenReturn(new TraineeResponseDto());

        ResponseEntity<TraineeResponseDto> response = traineeController.updateTrainee(username, password, updateRequestDto);

        verify(traineeService).updateTrainee(username, password, updateRequestDto);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testGetTraineeTrainingsList() {
        String traineeUsername = "testUser";
        String password = "testPassword";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String trainerFirstName = "John";
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Cardio");


        List<TrainingDto> trainingsList = new ArrayList<>();
        when(traineeService.getTraineeTrainingsList(traineeUsername, password, periodFrom, periodTo, trainerFirstName, trainingType))
                .thenReturn(trainingsList);

        ResponseEntity<List<TrainingDto>> response = traineeController.getTraineeTrainingsList(
                traineeUsername, password, periodFrom, periodTo, trainerFirstName, trainingType);

        verify(traineeService).getTraineeTrainingsList(
                traineeUsername, password, periodFrom, periodTo, trainerFirstName, trainingType);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testGetAvailableTrainersForTrainee() {
        String username = "testUser";
        String password = "testPassword";
        String traineeUsername = "traineeUser";

        List<TrainerListResponseDto> availableTrainers = new ArrayList<>();
        when(traineeService.getAvailableTrainersForTrainee(username, password, traineeUsername))
                .thenReturn(availableTrainers);

        ResponseEntity<List<TrainerListResponseDto>> response = traineeController.getAvailableTrainersForTrainee(
                username, password, traineeUsername);

        verify(traineeService).getAvailableTrainersForTrainee(username, password, traineeUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testUpdateTraineeTrainers() {
        String username = "testUser";
        String password = "testPassword";
        List<Trainer> trainers = new ArrayList<>();

        List<TrainerListResponseDto> updatedTrainers = new ArrayList<>();
        when(traineeService.updateTraineeTrainers(username, password, trainers))
                .thenReturn(updatedTrainers);

        ResponseEntity<List<TrainerListResponseDto>> response = traineeController.updateTraineeTrainers(
                username, password, trainers);

        verify(traineeService).updateTraineeTrainers(username, password, trainers);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testChangeActiveStatus() {
        String username = "testUser";
        String password = "testPassword";
        boolean activeStatus = true;

        ResponseEntity<Void> response = traineeController.changeActiveStatus(username, password, activeStatus);

        verify(traineeService).changeActiveStatus(username, password, activeStatus);

        assert response.getStatusCode() == HttpStatus.valueOf(204);
    }
}
