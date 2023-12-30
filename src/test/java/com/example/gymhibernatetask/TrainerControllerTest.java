package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainerController;
import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainerProfile() {
        String searchUsername = "searchUser";

        when(trainerService.selectTrainerProfile(searchUsername)).thenReturn(new TrainerResponseDto());

        ResponseEntity<TrainerResponseDto> response = trainerController.getTrainerProfile(searchUsername);

        verify(trainerService).selectTrainerProfile(searchUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainer() {
        String username = "testUser";
        UpdateTrainerRequestDto updateRequestDto = new UpdateTrainerRequestDto();

        when(trainerService.updateTrainer(username, updateRequestDto)).thenReturn(new TrainerResponseDto());

        ResponseEntity<TrainerResponseDto> response = trainerController.updateTrainer(username, updateRequestDto);

        verify(trainerService).updateTrainer(username, updateRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTrainerTrainingsList() {
        String trainerUsername = "testUser";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String traineeName = "John";
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Cardio");

        List<TrainingDto> trainingsList = List.of();
        when(trainerService.getTrainerTrainingsList(trainerUsername, periodFrom, periodTo, traineeName, trainingType))
                .thenReturn(trainingsList);

        ResponseEntity<List<TrainingDto>> response = trainerController.getTrainerTrainingsList(
                trainerUsername, periodFrom, periodTo, traineeName, trainingType);

        verify(trainerService).getTrainerTrainingsList(
                trainerUsername, periodFrom, periodTo, traineeName, trainingType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeActiveStatus() {
        String username = "testUser";
        boolean activeStatus = true;

        ResponseEntity<Void> response = trainerController.changeActiveStatus(username, activeStatus);

        verify(trainerService).changeActiveStatus(username, activeStatus);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
