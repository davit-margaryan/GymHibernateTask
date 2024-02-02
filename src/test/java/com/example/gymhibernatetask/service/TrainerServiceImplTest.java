package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.impl.TrainerServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private UtilService utilService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectTraineeProfile_success() {
        String searchUsername = "existingUser";

        Trainer mockTrainer = mock(Trainer.class);
        when(trainerRepository.getTrainerByUserUsername(searchUsername)).thenReturn(Optional.of(mockTrainer));

        TrainerResponseDto result = trainerService.selectTrainerProfile(searchUsername);

        assertNotNull(result);
        verify(trainerRepository, times(1)).getTrainerByUserUsername(searchUsername);
    }

    @Test
    void selectTrainerProfile_failure_notFound() {
        String searchUsername = "nonExistentUser";

        when(trainerRepository.getTrainerByUserUsername(searchUsername)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trainerService.selectTrainerProfile(searchUsername));
    }

    @Test
    void changeActiveStatus_success() {
        String loggedInUsername = "loggedInUser";
        boolean activeStatus = true;

        Trainer mockTrainer = mock(Trainer.class);
        User mockUser = mock(User.class);

        when(trainerRepository.getTrainerByUserUsername(loggedInUsername)).thenReturn(Optional.of(mockTrainer));
        when(mockTrainer.getUser()).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(loggedInUsername);

        trainerService.changeActiveStatus(loggedInUsername, activeStatus);

        verify(mockUser, times(1)).setActive(activeStatus);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void getTraineeTrainingsList_success() {
        String traineeUsername = "traineeUser";
        Date periodFrom = mock(Date.class);
        Date periodTo = mock(Date.class);

        Trainer mockTrainer = mock(Trainer.class);
        User mockUser = mock(User.class);
        TrainingType mockTrainingType = mock(TrainingType.class);

        when(trainerRepository.getTrainerByUserUsername(traineeUsername)).thenReturn(Optional.of(mockTrainer));
        when(mockTrainer.getUser()).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(traineeUsername);
        when(mockTrainingType.getTrainingTypeName()).thenReturn("Body");

        List<Training> mockTrainings = Arrays.asList(mock(Training.class), mock(Training.class));
        when(trainingRepository.findByTrainerAndCriteria(mockTrainer, periodFrom, periodTo, mockUser.getFirstName(), mockTrainingType.getTrainingTypeName()))
                .thenReturn(mockTrainings);

        List<TrainingDto> result = trainerService.getTrainerTrainingsList(traineeUsername, periodFrom, periodTo, mockUser.getFirstName(), mockTrainingType);

        verify(trainerRepository, times(1)).getTrainerByUserUsername(traineeUsername);
        verify(trainingRepository, times(1)).findByTrainerAndCriteria(mockTrainer, periodFrom, periodTo, mockUser.getFirstName(), mockTrainingType.getTrainingTypeName());

        assertNotNull(result);
        assertEquals(mockTrainings.size(), result.size());
    }
}
