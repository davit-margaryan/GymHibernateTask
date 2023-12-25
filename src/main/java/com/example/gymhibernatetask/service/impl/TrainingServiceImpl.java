package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.exception.AuthenticationException;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.service.TrainingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final LoginService loginService;
    private final TrainingRepository trainingRepository;
    private final Counter createdTrainingCount;


    public TrainingServiceImpl(TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository,
                               LoginService loginService,
                               TrainingRepository trainingRepository,
                               MeterRegistry meterRegistry) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.loginService = loginService;
        this.trainingRepository = trainingRepository;
        this.createdTrainingCount = Counter.builder("created_training")
                .description("Number of successful created trainings")
                .register(meterRegistry);
    }

    @Transactional
    @Override
    public void createTraining(String username, String password, CreateTrainingRequestDto requestDto) {
        if (!loginService.login(username, password)) {
            logger.warn("Authentication failed");
            throw new AuthenticationException("Authentication failed");
        }
        validateCreateTrainingRequest(requestDto);
        Training training = new Training();

        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername(requestDto.getTraineeUsername());
        Optional<Trainer> trainerByUserUsername = trainerRepository.getTrainerByUserUsername(requestDto.getTrainerUsername());
        if (trainerByUserUsername.isEmpty() && traineeByUserUsername.isEmpty()) {
            throw new InvalidInputException("Trainee or Trainer does not exists ");
        }
        Trainee trainee = traineeByUserUsername.get();
        Trainer trainer = trainerByUserUsername.get();

        List<Trainer> trainers = trainee.getTrainers();
        trainers.add(trainer);

        List<Trainee> trainees = trainer.getTrainees();
        trainees.add(trainee);

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        trainee.setTrainers(trainers);
        trainer.setTrainees(trainees);

        training.setName(requestDto.getTrainingName());
        training.setTrainingType(trainerByUserUsername.get().getSpecialization());
        training.setDate(requestDto.getDate());
        training.setDuration(requestDto.getDuration());

        trainingRepository.save(training);
        createdTrainingCount.increment();
        logger.info("Training created successfully.");
    }

    public void validateCreateTrainingRequest(CreateTrainingRequestDto createRequestDto) {
        if (createRequestDto == null) {
            logger.error("Create training request is null.");
            throw new InvalidInputException("Create training request cannot be null");
        }

        validateTrainingDate(createRequestDto.getDate());
        validateTrainingDuration(createRequestDto.getDuration());
    }

    private void validateTrainingDate(Date trainingDate) {
        if (trainingDate == null || trainingDate.before(new Date())) {
            throw new InvalidInputException("Training date must be in the future.");
        }
    }

    private void validateTrainingDuration(Number trainingDuration) {
        if (trainingDuration == null || trainingDuration.intValue() <= 0) {
            throw new InvalidInputException("Training duration must be a positive integer.");
        }
    }
}
