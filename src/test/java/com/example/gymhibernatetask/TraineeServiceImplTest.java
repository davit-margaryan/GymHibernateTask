package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.exception.AuthenticationException;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.*;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.service.impl.TraineeServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
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
    private LoginService loginService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_success() {
        CreateTraineeRequestDto requestDto = mock(CreateTraineeRequestDto.class);
        User mockedUser = mock(User.class);
        Trainee mockedTrainee = mock(Trainee.class);
        CreateResponseDto mockedResponseDto = mock(CreateResponseDto.class);

        when(userService.createUser(requestDto)).thenReturn(mockedUser);
        when(utilService.generateRandomPassword(10)).thenReturn("randomPassword");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockedTrainee);
        when(mockedUser.getUsername()).thenReturn("john.doe");
        when(mockedUser.getPassword()).thenReturn("randomPassword");
        when(mockedResponseDto.getUsername()).thenReturn("john.doe");
        when(mockedResponseDto.getPassword()).thenReturn("randomPassword");

        CreateResponseDto responseDto = traineeService.createTrainee(requestDto);

        verify(traineeRepository, times(1)).save(any(Trainee.class));
        verify(userService, times(1)).createUser(requestDto);
        assertEquals(mockedResponseDto.getPassword(), responseDto.getPassword());
        assertEquals(mockedResponseDto.getUsername(), responseDto.getUsername());
    }

    @Test
    void createTrainee_failure() {
        CreateTraineeRequestDto requestDto = mock(CreateTraineeRequestDto.class);

        when(userService.createUser(requestDto)).thenThrow(new RuntimeException("User creation failed"));

        Assertions.assertThrows(RuntimeException.class, () -> traineeService.createTrainee(requestDto));
    }

    @Test
    void getAllTrainee_success() {
        String username = "john.doe";
        String password = "password";

        when(loginService.login(username, password)).thenReturn(true);
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Trainee> trainees = traineeService.getAllTrainee(username, password);

        verify(traineeRepository, times(1)).findAll();
        assertNotNull(trainees);
        Assertions.assertTrue(trainees.isEmpty());
    }

    @Test
    void getAllTrainee_failure() {
        String username = "john.doe";
        String password = "incorrectPassword";

        when(loginService.login(username, password)).thenReturn(false);

        Assertions.assertThrows(AuthenticationException.class, () -> traineeService.getAllTrainee(username, password));
    }

    @Test
    void deleteTrainee_success() {
        String username = "admin";
        String password = "adminPassword";
        String deleteUsername = "john.doe";

        when(loginService.login(username, password)).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(deleteUsername)).thenReturn(Optional.of(mock(Trainee.class)));

        assertDoesNotThrow(() -> traineeService.deleteTrainee(username, password, deleteUsername));

        verify(traineeRepository, times(1)).deleteTraineeByUserUsername(deleteUsername);
    }

    @Test
    void deleteTrainee_failure_notFound() {
        String username = "admin";
        String password = "adminPassword";
        String deleteUsername = "nonExistentUser";

        when(loginService.login(username, password)).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(deleteUsername)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee(username, password, deleteUsername));
    }

    @Test
    void selectTraineeProfile_success() {
        String username = "admin";
        String password = "adminPassword";
        String searchUsername = "existingUser";

        when(loginService.login(username, password)).thenReturn(true);

        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.getTraineeByUserUsername(searchUsername)).thenReturn(Optional.of(expectedTrainee));

        TraineeResponseDto result = traineeService.selectTraineeProfile(username, password, searchUsername);

        assertNotNull(result);
        verify(loginService, times(1)).login(username, password);
        verify(traineeRepository, times(1)).getTraineeByUserUsername(searchUsername);
    }

    @Test
    void selectTraineeProfile_failure_notFound() {
        String username = "admin";
        String password = "adminPassword";
        String searchUsername = "nonExistentUser";

        when(loginService.login(username, password)).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(searchUsername)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> traineeService.selectTraineeProfile(username, password, searchUsername));
    }

    @Test
    void updateTrainee_success() {
        String username = "admin";
        String password = "adminPassword";
        UpdateTraineeRequestDto updateRequestDto = new UpdateTraineeRequestDto();
        updateRequestDto.setUsername("newUsername");
        updateRequestDto.setFirstName("NewFirstName");
        updateRequestDto.setLastName("NewLastName");
        updateRequestDto.setActive(true);
        updateRequestDto.setAddress("NewAddress");

        when(loginService.login(username, password)).thenReturn(true);

        Trainee expectedTrainee = new Trainee();
        User user = new User();
        expectedTrainee.setUser(user);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(expectedTrainee));

        TraineeResponseDto result = traineeService.updateTrainee(username, password, updateRequestDto);

        assertNotNull(result);
        verify(loginService, times(1)).login(username, password);
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

        when(loginService.login(anyString(), anyString())).thenReturn(true);

        Trainee existingTrainee = new Trainee();
        when(traineeRepository.getTraineeByUserUsername("existingUsername")).thenReturn(Optional.of(existingTrainee));

        assertThrows(NotFoundException.class, () -> traineeService.updateTrainee("loggedInUser", "password", updateRequestDto));
    }

    @Test
    void changeActiveStatus_success() {
        String loggedInUsername = "loggedInUser";
        String password = "password";
        boolean activeStatus = true;

        when(loginService.login(loggedInUsername, password)).thenReturn(true);

        Trainee existingTrainee = mock(Trainee.class);
        User existingUser = mock(User.class);

        when(traineeRepository.getTraineeByUserUsername(loggedInUsername)).thenReturn(Optional.of(existingTrainee));

        when(existingTrainee.getUser()).thenReturn(existingUser);
        when(existingUser.getUsername()).thenReturn(loggedInUsername);
        when(existingUser.getPassword()).thenReturn(password);

        traineeService.changeActiveStatus(loggedInUsername, password, activeStatus);

        verify(existingUser, times(1)).setActive(activeStatus);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateTraineeTrainers_success() {
        String loggedInUsername = "loggedInUser";
        String password = "password";

        when(loginService.login(loggedInUsername, password)).thenReturn(true);

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

        List<TrainerListResponseDto> result = traineeService.updateTraineeTrainers(loggedInUsername, password, trainers);

        verify(existingTrainee, times(1)).getTrainers();
        verify(existingTrainee, times(1)).setTrainers(trainers);
        verify(traineeRepository, times(1)).save(existingTrainee);

        assertNotNull(result);
    }


    @Test
    void getTraineeTrainingsList_success() {
        String traineeUsername = "traineeUser";
        String password = "password";
        Date periodFrom = new Date();
        Date periodTo = new Date();

        Trainee trainee = mock(Trainee.class);
        User user = mock(User.class);
        when(trainee.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(traineeUsername);
        when(loginService.login(traineeUsername, password)).thenReturn(true);

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
                traineeUsername, password, periodFrom, periodTo, user.getFirstName(), trainingType);

        verify(loginService, times(1)).login(traineeUsername, password);
        verify(traineeRepository, times(1)).getTraineeByUserUsername(traineeUsername);
        verify(trainingRepository, times(1)).findByTraineeAndCriteria(
                trainee, periodFrom, periodTo, user.getFirstName(), trainingType.getTrainingTypeName());

        assertNotNull(result);
        assertEquals(mockTrainings.size(), result.size());
    }


    @Test
    void getAvailableTrainersForTrainee_success() {
        String loggedInUsername = "loggedInUser";
        String password = "password";

        Trainee trainee = mock(Trainee.class);
        User user = mock(User.class);
        when(trainee.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(loggedInUsername);
        when(user.getPassword()).thenReturn(password);

        Trainer trainer1 = mock(Trainer.class);
        when(trainer1.getUser()).thenReturn(user);

        Trainer trainer2 = mock(Trainer.class);
        when(trainer2.getUser()).thenReturn(user);

        List<Trainer> allActiveTrainers = Arrays.asList(trainer1, trainer2);

        when(loginService.login(loggedInUsername, password)).thenReturn(true);

        when(traineeRepository.getTraineeByUserUsername(loggedInUsername)).thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllActiveTrainers()).thenReturn(allActiveTrainers);

        List<TrainerListResponseDto> result = traineeService.getAvailableTrainersForTrainee(loggedInUsername, password, loggedInUsername);

        verify(loginService, times(1)).login(loggedInUsername, password);
        verify(traineeRepository, times(1)).getTraineeByUserUsername(loggedInUsername);
        verify(trainerRepository, times(1)).findAllActiveTrainers();

        assertEquals(2, result.size());
    }
}
