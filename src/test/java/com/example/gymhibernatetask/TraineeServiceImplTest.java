package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.TraineeResponseDto;
import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTraineeRequestDto;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.*;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.service.impl.TraineeServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private UtilService utilService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Spy
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteTrainee_whenTraineeExists() {
        String username = "testUser";
        User user = mock(User.class);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(username);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteTrainee_failure_notFound() {
        String deleteUsername = "nonExistentUser";

        when(traineeRepository.getTraineeByUserUsername(deleteUsername)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee(deleteUsername));
    }

    @Test
    void selectTraineeProfile_success() {
        String searchUsername = "existingUser";

        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.getTraineeByUserUsername(searchUsername)).thenReturn(Optional.of(expectedTrainee));

        TraineeResponseDto result = traineeService.selectTraineeProfile(searchUsername);

        assertNotNull(result);
        verify(traineeRepository, times(1)).getTraineeByUserUsername(searchUsername);
    }

    @Test
    void selectTraineeProfile_failure_notFound() {
        String searchUsername = "nonExistentUser";

        when(traineeRepository.getTraineeByUserUsername(searchUsername)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> traineeService.selectTraineeProfile(searchUsername));
    }

    @Test
    void updateTrainee_success() {
        String username = "admin";
        UpdateTraineeRequestDto updateRequestDto = new UpdateTraineeRequestDto();
        updateRequestDto.setUsername("newUsername");
        updateRequestDto.setFirstName("NewFirstName");
        updateRequestDto.setLastName("NewLastName");
        updateRequestDto.setActive(true);
        updateRequestDto.setAddress("NewAddress");

        Trainee expectedTrainee = new Trainee();
        User user = new User();
        expectedTrainee.setUser(user);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(expectedTrainee));

        TraineeResponseDto result = traineeService.updateTrainee(username, updateRequestDto);

        assertNotNull(result);
        verify(traineeRepository, times(1)).getTraineeByUserUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
        verify(traineeRepository, times(1)).save(any(Trainee.class));

    }


    @Test
    void updateTrainee_failure_invalidInput() {
        UpdateTraineeRequestDto updateRequestDto = new UpdateTraineeRequestDto();
        updateRequestDto.setUsername("existingUsername");
        updateRequestDto.setFirstName("John");
        updateRequestDto.setLastName("Doe");

        Trainee existingTrainee = new Trainee();
        when(traineeRepository.getTraineeByUserUsername("existingUsername")).thenReturn(Optional.of(existingTrainee));

        assertThrows(NotFoundException.class, () -> traineeService.updateTrainee("loggedInUser", updateRequestDto));
    }

    @Test
    void changeActiveStatus_success() {
        String loggedInUsername = "loggedInUser";
        boolean activeStatus = true;

        Trainee existingTrainee = mock(Trainee.class);
        User existingUser = mock(User.class);

        when(traineeRepository.getTraineeByUserUsername(loggedInUsername)).thenReturn(Optional.of(existingTrainee));

        when(existingTrainee.getUser()).thenReturn(existingUser);
        when(existingUser.getUsername()).thenReturn(loggedInUsername);

        traineeService.changeActiveStatus(loggedInUsername, activeStatus);

        verify(existingUser, times(1)).setActive(activeStatus);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateTraineeTrainers_success() {
        String loggedInUsername = "loggedInUser";

        Trainee existingTrainee = mock(Trainee.class);

        when(traineeRepository.getTraineeByUserUsername(loggedInUsername)).thenReturn(Optional.of(existingTrainee));

        List<Trainer> trainers = new ArrayList<>();
        Trainer trainer1 = mock(Trainer.class);
        Trainer trainer2 = mock(Trainer.class);
        trainers.add(trainer1);
        trainers.add(trainer2);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(trainer1.getUser()).thenReturn(user1);
        when(trainer2.getUser()).thenReturn(user2);

        List<TrainerListResponseDto> result = traineeService.updateTraineeTrainers(loggedInUsername, trainers);

        verify(existingTrainee, times(1)).getTrainers();
        verify(existingTrainee, times(1)).setTrainers(trainers);
        verify(traineeRepository, times(1)).save(existingTrainee);

        assertNotNull(result);
    }


    @Test
    void getTraineeTrainingsList_success() {
        String traineeUsername = "traineeUser";
        Date periodFrom = new Date();
        Date periodTo = new Date();

        Trainee trainee = mock(Trainee.class);
        User user = mock(User.class);
        when(trainee.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(traineeUsername);

        when(traineeRepository.getTraineeByUserUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        TrainingType trainingType = mock(TrainingType.class);
        when(trainingType.getTrainingTypeName()).thenReturn("Body");

        List<Training> mockTrainings = Arrays.asList(
                new Training(),
                new Training()
        );
        when(trainingRepository.findByTraineeAndCriteria(
                trainee, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName()))
                .thenReturn(mockTrainings);

        List<TrainingDto> result = traineeService.getTraineeTrainingsList(
                traineeUsername, periodFrom, periodTo, user.getFirstName(), trainingType);

        verify(traineeRepository, times(1)).getTraineeByUserUsername(traineeUsername);
        verify(trainingRepository, times(1)).findByTraineeAndCriteria(
                trainee, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName());

        assertNotNull(result);
        assertEquals(mockTrainings.size(), result.size());
    }


    @Test
    void getAvailableTrainersForTrainee_success() {
        String loggedInUsername = "loggedInUser";

        Trainee trainee = mock(Trainee.class);
        User user = mock(User.class);
        when(trainee.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(loggedInUsername);

        Trainer trainer1 = mock(Trainer.class);
        when(trainer1.getUser()).thenReturn(user);

        Trainer trainer2 = mock(Trainer.class);
        when(trainer2.getUser()).thenReturn(user);

        List<Trainer> allActiveTrainers = Arrays.asList(trainer1, trainer2);

        when(traineeRepository.getTraineeByUserUsername(loggedInUsername)).thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllActiveTrainers()).thenReturn(allActiveTrainers);

        List<TrainerListResponseDto> result = traineeService.getAvailableTrainersForTrainee(loggedInUsername);

        verify(traineeRepository, times(1)).getTraineeByUserUsername(loggedInUsername);
        verify(trainerRepository, times(1)).findAllActiveTrainers();

        assertEquals(2, result.size());
    }
}
