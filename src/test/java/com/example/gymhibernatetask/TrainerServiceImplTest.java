package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.service.impl.TrainerServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private UtilService utilService;

    @Mock
    private LoginService loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_successful() {
        CreateTrainerRequestDto requestDto = mock(CreateTrainerRequestDto.class);
        User mockedUser = mock(User.class);
        Trainer mockedTrainer = mock(Trainer.class);
        CreateResponseDto mockedResponseDto = mock(CreateResponseDto.class);

        when(userService.createUser(requestDto)).thenReturn(mockedUser);
        when(utilService.generateRandomPassword(10)).thenReturn("randomPassword");
        when(trainerRepository.save(any(Trainer.class))).thenReturn(mockedTrainer);
        when(mockedUser.getUsername()).thenReturn("john.doe");
        when(mockedUser.getPassword()).thenReturn("randomPassword");
        when(mockedResponseDto.getUsername()).thenReturn("john.doe");
        when(mockedResponseDto.getPassword()).thenReturn("randomPassword");

        CreateResponseDto responseDto = trainerService.createTrainer(requestDto);

        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(userService, times(1)).createUser(requestDto);
        assertEquals(mockedResponseDto.getPassword(), responseDto.getPassword());
        assertEquals(mockedResponseDto.getUsername(), responseDto.getUsername());
    }

    @Test
    void createTrainer_failure() {
        CreateTrainerRequestDto requestDto = mock(CreateTrainerRequestDto.class);

        when(userService.createUser(requestDto)).thenThrow(new RuntimeException("User creation failed"));

        Assertions.assertThrows(RuntimeException.class, () -> trainerService.createTrainer(requestDto));
    }

    @Test
    void updateTrainee_success() {
        String username = "admin";
        String password = "adminPassword";
        UpdateTrainerRequestDto updateRequestDto = new UpdateTrainerRequestDto();
        updateRequestDto.setUsername("newUsername");
        updateRequestDto.setFirstName("NewFirstName");
        updateRequestDto.setLastName("NewLastName");
        updateRequestDto.setActive(true);

        when(loginService.login(username, password)).thenReturn(true);

        Trainer expectedTrainer = new Trainer();
        User user = new User();
        expectedTrainer.setUser(user);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(expectedTrainer));

        TrainerResponseDto result = trainerService.updateTrainer(username, password, updateRequestDto);

        assertNotNull(result);
        verify(loginService, times(1)).login(username, password);
        verify(trainerRepository, times(1)).getTrainerByUserUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));

    }

    @Test
    void selectTraineeProfile_success() {
        String username = "admin";
        String password = "adminPassword";
        String searchUsername = "existingUser";

        when(loginService.login(username, password)).thenReturn(true);

        Trainer expectedTrainer = new Trainer();
        when(trainerRepository.getTrainerByUserUsername(searchUsername)).thenReturn(Optional.of(expectedTrainer));

        TrainerResponseDto result = trainerService.selectTrainerProfile(username, password, searchUsername);

        assertNotNull(result);
        verify(loginService, times(1)).login(username, password);
        verify(trainerRepository, times(1)).getTrainerByUserUsername(searchUsername);
    }

    @Test
    void selectTrainerProfile_failure_notFound() {
        String username = "admin";
        String password = "adminPassword";
        String searchUsername = "nonExistentUser";

        when(loginService.login(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(searchUsername)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trainerService.selectTrainerProfile(username, password, searchUsername));
    }

    @Test
    void changeActiveStatus_success() {
        String loggedInUsername = "loggedInUser";
        String password = "password";
        boolean activeStatus = true;

        when(loginService.login(loggedInUsername, password)).thenReturn(true);

        Trainer existingTrainer = mock(Trainer.class);
        User existingUser = mock(User.class);

        when(trainerRepository.getTrainerByUserUsername(loggedInUsername)).thenReturn(Optional.of(existingTrainer));

        when(existingTrainer.getUser()).thenReturn(existingUser);
        when(existingUser.getUsername()).thenReturn(loggedInUsername);
        when(existingUser.getPassword()).thenReturn(password);

        trainerService.changeActiveStatus(loggedInUsername, password, activeStatus);

        verify(existingUser, times(1)).setActive(activeStatus);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void getTraineeTrainingsList_success() {
        String traineeUsername = "traineeUser";
        String password = "password";
        Date periodFrom = new Date();
        Date periodTo = new Date();

        Trainer trainer = mock(Trainer.class);
        User user = mock(User.class);
        when(trainer.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(traineeUsername);
        when(loginService.login(traineeUsername, password)).thenReturn(true);

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
                traineeUsername, password, periodFrom, periodTo, user.getFirstName(), trainingType);

        verify(loginService, times(1)).login(traineeUsername, password);
        verify(trainerRepository, times(1)).getTrainerByUserUsername(traineeUsername);
        verify(trainingRepository, times(1)).findByTrainerAndCriteria(
                trainer, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName());

        assertNotNull(result);
        assertEquals(mockTrainings.size(), result.size());
    }

}
