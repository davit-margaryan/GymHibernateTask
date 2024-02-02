package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TrainingServiceImplTest {
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);
        Date futureDate = mock(Date.class);

        when(requestDto.getTraineeUsername()).thenReturn("testTrainee");
        when(requestDto.getTrainerUsername()).thenReturn("testTrainer");
        when(requestDto.getDate()).thenReturn(futureDate);
        when(requestDto.getDuration()).thenReturn(1500);

        Trainee mockTrainee = mock(Trainee.class);
        Trainer mockTrainer = mock(Trainer.class);
        User mockUser = mock(User.class);

        when(mockTrainee.getTrainers()).thenReturn(new ArrayList<>());
        when(mockTrainer.getUser()).thenReturn(mockUser);
        when(mockTrainer.getTrainees()).thenReturn(new ArrayList<>());
        when(mockUser.getFirstName()).thenReturn("firstName");
        when(mockUser.getLastName()).thenReturn("lastName");

        when(traineeRepository.getTraineeByUserUsername("testTrainee")).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.getTrainerByUserUsername("testTrainer")).thenReturn(Optional.of(mockTrainer));

        TrainerWorkloadRequest result = trainingService.createTraining(requestDto);

        verify(trainingRepository, times(1)).save(any(Training.class));
        assertTrue(result.isActive());
    }

    @Test
    void testCreateTraining_InvalidInputException() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTraineeUsername()).thenReturn("testTrainee");
        when(traineeRepository.getTraineeByUserUsername("testTrainee")).thenReturn(Optional.empty());

        assertThrows(InvalidInputException.class, () -> {
            trainingService.createTraining(requestDto);
        });
    }
}