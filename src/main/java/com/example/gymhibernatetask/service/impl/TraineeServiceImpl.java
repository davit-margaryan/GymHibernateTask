package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.exception.AuthenticationException;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.*;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.util.UtilService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final String FAIL = "Authentication failed";
    private final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final UtilService utilService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final LoginService loginService;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TraineeServiceImpl(
            TraineeRepository traineeRepository,
            UserService userService,
            UtilService utilService,
            UserRepository userRepository, TrainingRepository trainingRepository, LoginService loginService, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
        this.utilService = utilService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.loginService = loginService;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional
    public CreateResponseDto createTrainee(CreateTraineeRequestDto traineeRequestDto) {
        logger.info("Creating trainee with request: {}", traineeRequestDto);

        Trainee trainee = new Trainee();
        User user = userService.createUser(traineeRequestDto);
        trainee.setUser(user);
        setOptionalFields(trainee, traineeRequestDto);
        traineeRepository.save(trainee);

        logger.info("Trainee created successfully. Username: {}", user.getUsername());
        return new CreateResponseDto(user.getUsername(), user.getPassword());
    }

    @Override
    public List<Trainee> getAllTrainee(String username, String password) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Fetching all trainees.");
        return traineeRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTrainee(String username, String password, String deleteUsername) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Deleting trainee with username: {}", deleteUsername);

        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername(deleteUsername);
        if (traineeByUserUsername.isPresent()) {
            userRepository.delete(traineeByUserUsername.get().getUser());
            logger.info("Trainee deleted successfully. Username: {}", deleteUsername);
        } else {
            logger.warn("Trainee not found for deletion. Username: {}", deleteUsername);
            throw new NotFoundException("Trainee not found");
        }
    }

    @Override
    public TraineeResponseDto selectTraineeProfile(String username, String password, String searchUsername) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Fetching trainee by username: {}", searchUsername);

        Trainee trainee = getTraineeByUsername(searchUsername);

        logger.info("Trainee fetched successfully. Username: {}", searchUsername);
        return convertToTraineeDto(trainee);
    }

    @Override
    @Transactional
    public TraineeResponseDto updateTrainee(String username, String password, UpdateTraineeRequestDto updateRequestDto) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Updating trainee with request: {}", updateRequestDto);

        utilService.validateUpdateRequest(updateRequestDto);

        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setUsername(updateRequestDto.getUsername());
        user.setFirstName(updateRequestDto.getFirstName());
        user.setLastName(updateRequestDto.getLastName());
        user.setActive(updateRequestDto.isActive());

        if (updateRequestDto.getAddress() != null && !updateRequestDto.getAddress().trim().isEmpty()) {
            trainee.setAddress(updateRequestDto.getAddress());
        }

        if (updateRequestDto.getDateOfBirth() != null) {
            trainee.setDateOfBirth(updateRequestDto.getDateOfBirth());
        }

        if (updateRequestDto.isActive() != user.isActive()) {
            user.setActive(updateRequestDto.isActive());
        }

        userRepository.save(user);
        traineeRepository.save(trainee);

        logger.info("Trainee updated successfully. Username: {}", user.getUsername());
        return convertToTraineeDto(trainee);
    }

    @Override
    public void changeActiveStatus(String username, String password, boolean activeStatus) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Changing user status: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setActive(activeStatus);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<TrainerListResponseDto> updateTraineeTrainers(String username, String password, List<Trainer> trainers) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }

        Trainee trainee = getTraineeByUsername(username);
        trainee.setTrainers(trainers);

        traineeRepository.save(trainee);

        return trainee.getTrainers().stream().map(utilService::convertToTrainerDto).collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTraineeTrainingsList(String traineeUsername, String password,
                                                     Date periodFrom, Date periodTo,
                                                     String trainerFirstName, TrainingType trainingType) {
        if (!loginService.login(traineeUsername, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }

        Trainee trainee = getTraineeByUsername(traineeUsername);

        List<Training> trainings = trainingRepository.findByTraineeAndCriteria(trainee,
                periodFrom, periodTo, trainerFirstName, trainingType.getTrainingTypeName());

        logger.info("Trainee trainings fetched successfully. Username: {}", traineeUsername);

        return trainings.stream().map(utilService::convertToTrainingDto).collect(Collectors.toList());
    }


    @Override
    public List<TrainerListResponseDto> getAvailableTrainersForTrainee(String username, String password, String traineeUsername) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }

        Trainee trainee = getTraineeByUsername(traineeUsername);
        List<Trainer> allActiveTrainers = trainerRepository.findAllActiveTrainers();

        return allActiveTrainers.stream()
                .filter(trainer -> trainee.getTrainers() == null || !trainee.getTrainers().contains(trainer))
                .map(utilService::convertToTrainerDto)
                .collect(Collectors.toList());
    }

    private void setOptionalFields(Trainee trainee, CreateTraineeRequestDto traineeRequestDto) {
        if (traineeRequestDto.getDateOfBirth() != null) {
            trainee.setDateOfBirth(traineeRequestDto.getDateOfBirth());
        }

        if (traineeRequestDto.getAddress() != null && !traineeRequestDto.getAddress().trim().isEmpty()) {
            trainee.setAddress(traineeRequestDto.getAddress());
        }
    }


    public Trainee getTraineeByUsername(String username) {
        Optional<Trainee> optionalTrainee = traineeRepository.getTraineeByUserUsername(username);
        if (optionalTrainee.isEmpty()) {
            logger.warn("Trainee not found. Username: {}", username);
            throw new NotFoundException("Trainee not found for username: " + username);
        }
        return optionalTrainee.get();
    }

    public TraineeResponseDto convertToTraineeDto(Trainee trainee) {
        TraineeResponseDto traineeResponseDto = new TraineeResponseDto();
        if (trainee.getUser() != null) {
            traineeResponseDto.setFirstName(trainee.getUser().getFirstName());
            traineeResponseDto.setLastName(trainee.getUser().getLastName());
            traineeResponseDto.setActive(trainee.getUser().isActive());
        }
        traineeResponseDto.setDateOfBirth(trainee.getDateOfBirth());
        traineeResponseDto.setAddress(trainee.getAddress());
        traineeResponseDto.setTrainers(trainee.getTrainers());
        return traineeResponseDto;
    }

}
