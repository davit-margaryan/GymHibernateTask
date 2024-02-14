package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.TraineeResponseDto;
import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTraineeRequestDto;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.util.TransactionLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private TraineeService traineeService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TraineeController traineeController;

    @Test
    void testDeleteTrainee() {
        String deleteUsername = "deleteUser";
        Trainee trainee = mock(Trainee.class);
        User user = mock(User.class);

        lenient().when(user.getUsername()).thenReturn(deleteUsername);
        lenient().when(trainee.getUser()).thenReturn(user);

        when(traineeRepository.getTraineeByUserUsername(deleteUsername)).thenReturn(Optional.of(trainee));

        ResponseEntity<Void> response = traineeController.deleteTrainee(deleteUsername);

        verify(traineeService).deleteTrainee(deleteUsername);
        assert response.getStatusCode().equals(HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetTraineeProfile() {
        String searchUsername = "searchUser";
        when(traineeService.selectTraineeProfile(searchUsername)).thenReturn(mock(TraineeResponseDto.class));

        ResponseEntity<TraineeResponseDto> response = traineeController.getTraineeProfile(searchUsername);

        verify(traineeService).selectTraineeProfile(searchUsername);
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    void testUpdateTrainee() {
        String username = "testUser";
        UpdateTraineeRequestDto updateRequestDto = mock(UpdateTraineeRequestDto.class);
        when(traineeService.updateTrainee(username, updateRequestDto)).thenReturn(mock(TraineeResponseDto.class));
        ResponseEntity<TraineeResponseDto> response = traineeController.updateTrainee(username, updateRequestDto);
        verify(traineeService).updateTrainee(username, updateRequestDto);
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    void testGetTraineeTrainingsList() {
        String traineeUsername = "testUser";
        Date periodFrom = mock(Date.class);
        Date periodTo = mock(Date.class);
        String trainerFirstName = "John";
        TrainingType trainingType = mock(TrainingType.class);
        lenient().when(trainingType.getTrainingTypeName()).thenReturn("Cardio");

        when(traineeService.getTraineeTrainingsList(traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType))
                .thenReturn(Arrays.asList(mock(TrainingDto.class)));

        ResponseEntity<List<TrainingDto>> response = traineeController.getTraineeTrainingsList(
                traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType);

        verify(traineeService).getTraineeTrainingsList(
                traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType);

        assert response.getStatusCode().equals(HttpStatus.OK);
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
        String trainerUsername1 = "trainer1";
        String trainerUsername2 = "trainer2";
        List<String> trainersUsernames = Arrays.asList(trainerUsername1, trainerUsername2);
        List<Trainer> trainers = new ArrayList<>();
        TrainerListResponseDto mockDto = mock(TrainerListResponseDto.class);

        when(trainerRepository.getTrainerByUserUsername(trainerUsername1)).thenReturn(Optional.of(new Trainer()));
        when(trainerRepository.getTrainerByUserUsername(trainerUsername2)).thenReturn(Optional.of(new Trainer()));

        when(traineeService.updateTraineeTrainers(eq(username), anyList())).thenReturn(Collections.singletonList(mockDto));

        ResponseEntity<List<TrainerListResponseDto>> response = traineeController.updateTraineeTrainers(username, trainersUsernames);

        verify(trainerRepository).getTrainerByUserUsername(trainerUsername1);
        verify(trainerRepository).getTrainerByUserUsername(trainerUsername2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
