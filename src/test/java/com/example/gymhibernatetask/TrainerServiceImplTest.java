package com.example.gymhibernatetask;

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
import com.example.gymhibernatetask.service.UserService;
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

        Trainer expectedTrainer = new Trainer();
        when(trainerRepository.getTrainerByUserUsername(searchUsername)).thenReturn(Optional.of(expectedTrainer));

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


        Trainer existingTrainer = mock(Trainer.class);
        User existingUser = mock(User.class);

        when(trainerRepository.getTrainerByUserUsername(loggedInUsername)).thenReturn(Optional.of(existingTrainer));

        when(existingTrainer.getUser()).thenReturn(existingUser);
        when(existingUser.getUsername()).thenReturn(loggedInUsername);

        trainerService.changeActiveStatus(loggedInUsername, activeStatus);

        verify(existingUser, times(1)).setActive(activeStatus);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void getTraineeTrainingsList_success() {
        String traineeUsername = "traineeUser";
        Date periodFrom = new Date();
        Date periodTo = new Date();

        Trainer trainer = mock(Trainer.class);
        User user = mock(User.class);
        when(trainer.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(traineeUsername);

        when(trainerRepository.getTrainerByUserUsername(traineeUsername)).thenReturn(Optional.of(trainer));

        TrainingType trainingType = mock(TrainingType.class);
        when(trainingType.getTrainingTypeName()).thenReturn("Body");

        List<Training> mockTrainings = Arrays.asList(
                new Training(),
                new Training()
        );
        when(trainingRepository.findByTrainerAndCriteria(
                trainer, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName()))
                .thenReturn(mockTrainings);

        List<TrainingDto> result = trainerService.getTrainerTrainingsList(
                traineeUsername, periodFrom, periodTo, user.getFirstName(), trainingType);

        verify(trainerRepository, times(1)).getTrainerByUserUsername(traineeUsername);
        verify(trainingRepository, times(1)).findByTrainerAndCriteria(
                trainer, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName());

        assertNotNull(result);
        assertEquals(mockTrainings.size(), result.size());
    }

}
