package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.service.TrainingService;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkload;
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
    private final TrainingRepository trainingRepository;


    public TrainingServiceImpl(TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository,
                               TrainingRepository trainingRepository,
                               MeterRegistry meterRegistry) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    @Override
    public TrainerWorkload createTraining(CreateTrainingRequestDto requestDto) {
        validateCreateTrainingRequest(requestDto);
        Training training = new Training();

        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername(requestDto.getTraineeUsername());
        Optional<Trainer> trainerByUserUsername = trainerRepository.getTrainerByUserUsername(requestDto.getTrainerUsername());
        if (trainerByUserUsername.isEmpty() || traineeByUserUsername.isEmpty()) {
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
        logger.info("Training created successfully.");

        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(requestDto.getDate());
        trainerWorkload.setActive(true);
        trainerWorkload.setActionType("ADD");
        trainerWorkload.setUsername(requestDto.getTrainerUsername());
        trainerWorkload.setFirstName(trainer.getUser().getFirstName());
        trainerWorkload.setLastName(trainer.getUser().getLastName());

        return trainerWorkload;
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
