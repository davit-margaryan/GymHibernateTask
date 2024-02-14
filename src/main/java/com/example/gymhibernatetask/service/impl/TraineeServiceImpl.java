package com.example.gymhibernatetask.service.impl;

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
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.util.UtilService;
import jakarta.persistence.EntityManager;
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

    private final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final TraineeRepository traineeRepository;
    private final UtilService utilService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final EntityManager entityManager;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TraineeServiceImpl(
            TraineeRepository traineeRepository,
            UtilService utilService,
            UserRepository userRepository, TrainingRepository trainingRepository, EntityManager entityManager,
            TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.utilService = utilService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.entityManager = entityManager;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional
    public void deleteTrainee(String deleteUsername) {
        logger.info("Deleting trainee with username: {}", deleteUsername);

        Optional<Trainee> traineeOptional = traineeRepository.getTraineeByUserUsername(deleteUsername);

        if (traineeOptional.isEmpty()) {
            logger.warn("Trainee not found for deletion. Username: {}", deleteUsername);
            throw new NotFoundException("Trainee not found");
        }

        Trainee trainee = traineeOptional.get();
        Long traineeId = trainee.getId();
        Long userId = trainee.getUser().getId();

        String deleteFromTraineeTrainer = "DELETE FROM trainee_trainer WHERE trainee_id = :traineeId";
        entityManager.createNativeQuery(deleteFromTraineeTrainer)
                .setParameter("traineeId", traineeId)
                .executeUpdate();

        String deleteFromTraining = "DELETE FROM Training WHERE trainee_id = :traineeId";
        entityManager.createNativeQuery(deleteFromTraining)
                .setParameter("traineeId", traineeId)
                .executeUpdate();

        String deleteFromTrainee = "DELETE FROM Trainee WHERE id = :traineeId";
        entityManager.createNativeQuery(deleteFromTrainee)
                .setParameter("traineeId", traineeId)
                .executeUpdate();

        String deleteFromUser = "DELETE FROM User WHERE id = :userId";
        entityManager.createNativeQuery(deleteFromUser)
                .setParameter("userId", userId)
                .executeUpdate();

        logger.info("Trainee deleted successfully. Username: {}", deleteUsername);
    }

    @Override
    public TraineeResponseDto selectTraineeProfile(String searchUsername) {
        logger.info("Fetching trainee by username: {}", searchUsername);

        Trainee trainee = getTraineeByUsername(searchUsername);

        logger.info("Trainee fetched successfully. Username: {}", searchUsername);
        return new TraineeResponseDto(trainee);
    }

    @Override
    @Transactional
    public TraineeResponseDto updateTrainee(String username, UpdateTraineeRequestDto updateRequestDto) {
        logger.info("Updating trainee with request: {}", updateRequestDto);

        utilService.validateUpdateRequest(updateRequestDto);

        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setUsername(updateRequestDto.getUsername());
        user.setFirstName(updateRequestDto.getFirstName());
        user.setLastName(updateRequestDto.getLastName());
        user.setActive(updateRequestDto.isActive());

        trainee.setAddress(updateRequestDto.getAddress());
        trainee.setDateOfBirth(updateRequestDto.getDateOfBirth());

        userRepository.save(user);
        traineeRepository.save(trainee);

        logger.info("Trainee updated successfully. Username: {}", user.getUsername());
        return new TraineeResponseDto(trainee);
    }

    @Override
    public void changeActiveStatus(String username, boolean activeStatus) {
        logger.info("Changing user status: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setActive(activeStatus);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<TrainerListResponseDto> updateTraineeTrainers(String username, List<Trainer> trainers) {
        Trainee trainee = getTraineeByUsername(username);
        trainee.setTrainers(trainers);

        traineeRepository.save(trainee);

        return trainee.getTrainers().stream().map(TrainerListResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTraineeTrainingsList(String traineeUsername,
                                                     Date periodFrom, Date periodTo,
                                                     String trainerFirstName, TrainingType trainingType) {
        Trainee trainee = getTraineeByUsername(traineeUsername);

        String trainingTypeName = null;
        if (trainingType != null) {
            trainingTypeName = trainingType.getTrainingTypeName();
        }

        List<Training> trainings = trainingRepository.findByTraineeAndCriteria(trainee,
                periodFrom, periodTo, trainerFirstName, trainingTypeName);

        logger.info("Trainee trainings fetched successfully. Username: {}", traineeUsername);

        return trainings.stream().map(TrainingDto::new).collect(Collectors.toList());
    }


    @Override
    public List<TrainerListResponseDto> getAvailableTrainersForTrainee(String traineeUsername) {
        Trainee trainee = getTraineeByUsername(traineeUsername);
        List<Trainer> allActiveTrainers = trainerRepository.findAllActiveTrainers();

        return allActiveTrainers.stream()
                .filter(trainer -> trainee.getTrainers() == null || !trainee.getTrainers().contains(trainer))
                .map(TrainerListResponseDto::new)
                .collect(Collectors.toList());
    }

    public Trainee getTraineeByUsername(String username) {
        Optional<Trainee> optionalTrainee = traineeRepository.getTraineeByUserUsername(username);
        if (optionalTrainee.isEmpty()) {
            logger.warn("Trainee not found. Username: {}", username);
            throw new NotFoundException("Trainee not found for username: " + username);
        }
        return optionalTrainee.get();
    }
}
