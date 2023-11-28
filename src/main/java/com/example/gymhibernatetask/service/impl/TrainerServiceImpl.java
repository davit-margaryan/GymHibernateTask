package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.exception.AuthenticationException;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.service.TrainerService;
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
public class TrainerServiceImpl implements TrainerService {

    private static final String FAIL = "Authentication failed";
    private final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final LoginService loginService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final UtilService utilService;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository, UserService userService,
                              LoginService loginService, UserRepository userRepository,
                              TrainingRepository trainingRepository, UtilService utilService) {
        this.trainerRepository = trainerRepository;
        this.userService = userService;
        this.loginService = loginService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.utilService = utilService;
    }

    @Transactional
    @Override
    public CreateResponseDto createTrainer(CreateTrainerRequestDto trainerRequestDto) {
        logger.info("Creating trainer with request: {}", trainerRequestDto);

        Trainer trainer = new Trainer();
        User user = userService.createUser(trainerRequestDto);
        trainer.setUser(user);
        if (trainerRequestDto.getSpecialization() != null) {
            trainer.setSpecialization(trainerRequestDto.getSpecialization());
        }

        trainerRepository.save(trainer);
        logger.info("Trainer created successfully. Username: {}", user.getUsername());
        return new CreateResponseDto(user.getUsername(), user.getPassword());
    }

    @Override
    public TrainerResponseDto selectTrainerProfile(String username, String password, String searchUsername) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Fetching trainer by username: {}", searchUsername);

        Trainer trainer = getTrainerByUsername(searchUsername);

        logger.info("Trainer fetched successfully. Username: {}", searchUsername);
        return convertToTrainerDto(trainer);
    }

    @Override
    @Transactional
    public TrainerResponseDto updateTrainer(String username, String password, UpdateTrainerRequestDto updateRequestDto) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Updating trainer with request: {}", updateRequestDto);

        utilService.validateUpdateRequest(updateRequestDto);

        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();

        user.setUsername(updateRequestDto.getUsername());
        user.setFirstName(updateRequestDto.getFirstName());
        user.setLastName(updateRequestDto.getLastName());
        user.setActive(updateRequestDto.isActive());

        if (updateRequestDto.getSpecialization() != null && !updateRequestDto.getSpecialization().getTrainingTypeName().trim().isEmpty()) {
            trainer.setSpecialization(updateRequestDto.getSpecialization());
        }

        userRepository.save(user);
        trainerRepository.save(trainer);

        logger.info("Trainer updated successfully. Username: {}", user.getUsername());
        return convertToTrainerDto(trainer);
    }


    @Override
    public void changeActiveStatus(String username, String password, boolean activeStatus) {
        if (!loginService.login(username, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }
        logger.info("Changing user status: {}", username);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setActive(activeStatus);
        userRepository.save(user);
    }

    @Override
    public List<TrainingDto> getTrainerTrainingsList(String trainerUsername, String password,
                                                     Date periodFrom, Date periodTo, String traineeName, TrainingType trainingType) {
        if (!loginService.login(trainerUsername, password)) {
            logger.warn(FAIL);
            throw new AuthenticationException(FAIL);
        }

        Trainer trainer = getTrainerByUsername(trainerUsername);

        List<Training> trainings = trainingRepository.findByTrainerAndCriteria(trainer,
                periodFrom, periodTo, traineeName, trainingType.getTrainingTypeName());

        logger.info("Trainer trainings fetched successfully. Username: {}", trainerUsername);

        return trainings.stream().map(utilService::convertToTrainingDto).collect(Collectors.toList());
    }


    public Trainer getTrainerByUsername(String username) {
        Optional<Trainer> optionalTrainer = trainerRepository.getTrainerByUserUsername(username);

        if (optionalTrainer.isEmpty()) {
            logger.warn("Trainer not found for update. Username: {}", username);
            throw new NotFoundException("Trainer not found for username: " + username);
        }
        return optionalTrainer.get();
    }

    public TrainerResponseDto convertToTrainerDto(Trainer trainer) {
        TrainerResponseDto trainerResponseDto = new TrainerResponseDto();
        if (trainer.getUser() != null) {
            trainerResponseDto.setFirstName(trainer.getUser().getFirstName());
            trainerResponseDto.setLastName(trainer.getUser().getLastName());
            trainerResponseDto.setActive(trainer.getUser().isActive());
        }
        trainerResponseDto.setSpecialization(trainer.getSpecialization());
        trainerResponseDto.setTrainees(trainer.getTrainees());
        return trainerResponseDto;
    }
}
