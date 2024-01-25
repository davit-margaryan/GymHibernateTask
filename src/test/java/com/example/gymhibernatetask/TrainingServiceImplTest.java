package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.service.impl.TrainingServiceImpl;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
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
        CreateTrainingRequestDto requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername("testTrainee");
        requestDto.setTrainerUsername("testTrainer");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 10);
        Date futureDate = calendar.getTime();

        requestDto.setDate(futureDate);
        requestDto.setDuration(1500);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        trainer.setUser(user);

        trainee.setTrainers(new ArrayList<>());
        trainer.setTrainees(new ArrayList<>());

        when(traineeRepository.getTraineeByUserUsername("testTrainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.getTrainerByUserUsername("testTrainer")).thenReturn(Optional.of(trainer));

        TrainerWorkloadRequest result = trainingService.createTraining(requestDto);

        verify(trainingRepository, times(1)).save(any(Training.class));
        assertTrue(result.isActive());
    }

    @Test
    void testCreateTraining_InvalidInputException() {
        CreateTrainingRequestDto requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername("testTrainee");
        requestDto.setTrainerUsername("testTrainer");

        when(traineeRepository.getTraineeByUserUsername("testTrainee")).thenReturn(Optional.empty());

        assertThrows(InvalidInputException.class, () -> {
            trainingService.createTraining(requestDto);
        });
    }
}