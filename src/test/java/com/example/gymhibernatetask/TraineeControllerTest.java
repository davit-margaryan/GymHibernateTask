package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TraineeController;
import com.example.gymhibernatetask.dto.TraineeResponseDto;
import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTraineeRequestDto;
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
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testDeleteTrainee() {
        String deleteUsername = "deleteUser";

        ResponseEntity<Void> response = traineeController.deleteTrainee(deleteUsername);

        verify(traineeService).deleteTrainee(deleteUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(204);
    }

    @Test
    void testGetTraineeProfile() {
        String searchUsername = "searchUser";

        when(traineeService.selectTraineeProfile(searchUsername)).thenReturn(new TraineeResponseDto());

        ResponseEntity<TraineeResponseDto> response = traineeController.getTraineeProfile(searchUsername);

        verify(traineeService).selectTraineeProfile(searchUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testUpdateTrainee() {
        String username = "testUser";
        UpdateTraineeRequestDto updateRequestDto = new UpdateTraineeRequestDto();

        when(traineeService.updateTrainee(username, updateRequestDto)).thenReturn(new TraineeResponseDto());

        ResponseEntity<TraineeResponseDto> response = traineeController.updateTrainee(username, updateRequestDto);

        verify(traineeService).updateTrainee(username, updateRequestDto);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testGetTraineeTrainingsList() {
        String traineeUsername = "testUser";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String trainerFirstName = "John";
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Cardio");


        List<TrainingDto> trainingsList = new ArrayList<>();
        when(traineeService.getTraineeTrainingsList(traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType))
                .thenReturn(trainingsList);

        ResponseEntity<List<TrainingDto>> response = traineeController.getTraineeTrainingsList(
                traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType);

        verify(traineeService).getTraineeTrainingsList(
                traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testGetAvailableTrainersForTrainee() {
        String traineeUsername = "traineeUser";

        List<TrainerListResponseDto> availableTrainers = new ArrayList<>();
        when(traineeService.getAvailableTrainersForTrainee(traineeUsername))
                .thenReturn(availableTrainers);

        ResponseEntity<List<TrainerListResponseDto>> response = traineeController.getAvailableTrainersForTrainee(traineeUsername);

        verify(traineeService).getAvailableTrainersForTrainee(traineeUsername);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testUpdateTraineeTrainers() {
        String username = "testUser";
        List<Trainer> trainers = new ArrayList<>();

        List<TrainerListResponseDto> updatedTrainers = new ArrayList<>();
        when(traineeService.updateTraineeTrainers(username, trainers))
                .thenReturn(updatedTrainers);

        ResponseEntity<List<TrainerListResponseDto>> response = traineeController.updateTraineeTrainers(
                username, trainers);

        verify(traineeService).updateTraineeTrainers(username, trainers);

        assert response.getStatusCode() == HttpStatus.valueOf(200);
    }

    @Test
    void testChangeActiveStatus() {
        String username = "testUser";
        boolean activeStatus = true;

        ResponseEntity<Void> response = traineeController.changeActiveStatus(username, activeStatus);

        verify(traineeService).changeActiveStatus(username, activeStatus);

        assert response.getStatusCode() == HttpStatus.valueOf(204);
    }
}
