package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.exception.NotFoundException;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.TrainerService;
import com.example.gymhibernatetask.util.UtilService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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

    private final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final UtilService utilService;
    private final Counter updatedTrainersCount;


    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository, UserRepository userRepository,
                              TrainingRepository trainingRepository, UtilService utilService,
                              MeterRegistry meterRegistry) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.utilService = utilService;
        this.updatedTrainersCount = Counter.builder("updated_trainers")
                .description("Number of successful updated trainers")
                .register(meterRegistry);
    }

    @Override
    public TrainerResponseDto selectTrainerProfile(String searchUsername) {
        logger.info("Fetching trainer by username: {}", searchUsername);

        Trainer trainer = getTrainerByUsername(searchUsername);

        logger.info("Trainer fetched successfully. Username: {}", searchUsername);
        return new TrainerResponseDto(trainer);
    }

    @Override
    @Transactional
    public TrainerResponseDto updateTrainer(String username, UpdateTrainerRequestDto updateRequestDto) {
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

        updatedTrainersCount.increment();
        logger.info("Trainer updated successfully. Username: {}", user.getUsername());
        return new TrainerResponseDto(trainer);
    }


    @Override
    public void changeActiveStatus(String username, boolean activeStatus) {
        logger.info("Changing user status: {}", username);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setActive(activeStatus);
        userRepository.save(user);
    }

    @Override
    public List<TrainingDto> getTrainerTrainingsList(String trainerUsername,
                                                     Date periodFrom, Date periodTo, String traineeName, TrainingType trainingType) {

        Trainer trainer = getTrainerByUsername(trainerUsername);

        String trainingTypeName = null;
        if (trainingType != null) {
            trainingTypeName = trainingType.getTrainingTypeName();
        }

        List<Training> trainings = trainingRepository.findByTrainerAndCriteria(trainer,
                periodFrom, periodTo, traineeName, trainingTypeName);

        logger.info("Trainer trainings fetched successfully. Username: {}", trainerUsername);

        return trainings.stream().map(TrainingDto::new).collect(Collectors.toList());
    }


    public Trainer getTrainerByUsername(String username) {
        Optional<Trainer> optionalTrainer = trainerRepository.getTrainerByUserUsername(username);

        if (optionalTrainer.isEmpty()) {
            logger.warn("Trainer not found for update. Username: {}", username);
            throw new NotFoundException("Trainer not found for username: " + username);
        }
        return optionalTrainer.get();
    }

}
