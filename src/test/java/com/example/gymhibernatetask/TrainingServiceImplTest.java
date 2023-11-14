package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTraining_success() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);
        Trainee mockTrainee = mock(Trainee.class);
        Trainer mockTrainer = mock(Trainer.class);

        when(requestDto.getTraineeUsername()).thenReturn("traineeUser");
        when(requestDto.getTrainerUsername()).thenReturn("trainerUser");
        when(requestDto.getTrainingName()).thenReturn("Body Workout");
        when(requestDto.getDate()).thenReturn(new Date(System.currentTimeMillis() + 86400000));
        when(requestDto.getDuration()).thenReturn(60);

        when(traineeRepository.getTraineeByUserUsername(requestDto.getTraineeUsername())).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.getTrainerByUserUsername(requestDto.getTrainerUsername())).thenReturn(Optional.of(mockTrainer));

        trainingService.createTraining(requestDto);

        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void createTraining_failure_invalidTrainee() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTraineeUsername()).thenReturn("nonExistentTrainee");
        when(requestDto.getTrainerUsername()).thenReturn("trainerUser");
        when(requestDto.getTrainingName()).thenReturn("Body Workout");
        when(requestDto.getDate()).thenReturn(new Date(System.currentTimeMillis() + 86400000));
        when(requestDto.getDuration()).thenReturn(60);

        when(traineeRepository.getTraineeByUserUsername(requestDto.getTraineeUsername())).thenReturn(Optional.empty());

        assertThrows(InvalidInputException.class, () -> trainingService.createTraining(requestDto));
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void validateCreateTrainingRequest_success() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTrainingName()).thenReturn("Body Workout");
        when(requestDto.getDate()).thenReturn(new Date(System.currentTimeMillis() + 86400000));
        when(requestDto.getDuration()).thenReturn(60);

        trainingService.validateCreateTrainingRequest(requestDto);
    }

    @Test
    void validateCreateTrainingRequest_failure_nullRequest() {
        CreateTrainingRequestDto requestDto = null;

        assertThrows(InvalidInputException.class, () -> trainingService.validateCreateTrainingRequest(requestDto));
    }

    @Test
    void validateCreateTrainingRequest_failure_nullName() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTrainingName()).thenReturn(null);

        assertThrows(InvalidInputException.class, () -> trainingService.validateCreateTrainingRequest(requestDto));
    }

    @Test
    void validateCreateTrainingRequest_failure_emptyName() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTrainingName()).thenReturn("");

        assertThrows(InvalidInputException.class, () -> trainingService.validateCreateTrainingRequest(requestDto));
    }

    @Test
    void validateCreateTrainingRequest_failure_pastDate() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTrainingName()).thenReturn("Body Workout");
        when(requestDto.getDate()).thenReturn(new Date(System.currentTimeMillis() - 86400000));
        when(requestDto.getDuration()).thenReturn(60);

        assertThrows(InvalidInputException.class, () -> trainingService.validateCreateTrainingRequest(requestDto));
    }

    @Test
    void validateCreateTrainingRequest_failure_negativeDuration() {
        CreateTrainingRequestDto requestDto = mock(CreateTrainingRequestDto.class);

        when(requestDto.getTrainingName()).thenReturn("Body Workout");
        when(requestDto.getDate()).thenReturn(new Date(System.currentTimeMillis() + 86400000));
        when(requestDto.getDuration()).thenReturn(-60);

        assertThrows(InvalidInputException.class, () -> trainingService.validateCreateTrainingRequest(requestDto));
    }
}
