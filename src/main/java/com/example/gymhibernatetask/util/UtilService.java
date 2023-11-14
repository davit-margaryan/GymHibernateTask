package com.example.gymhibernatetask.util;

import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Component
public class UtilService {

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final Logger logger = LoggerFactory.getLogger(UtilService.class);
    private final UserRepository userRepository;

    @Autowired
    public UtilService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUsername(String firstName, String lastName, List<User> users) {
        String baseUsername = firstName + "." + lastName;
        String generatedUsername = baseUsername;
        int serialNumber = 1;

        while (usernameExists(users, generatedUsername)) {
            generatedUsername = baseUsername + "." + serialNumber;
            serialNumber++;
        }
        return generatedUsername;
    }

    public String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }

        return password.toString();
    }

    public boolean usernameExists(List<User> users, String username) {
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public TrainerListResponseDto convertToTrainerDto(Trainer trainer) {
        TrainerListResponseDto trainerDto = new TrainerListResponseDto();
        trainerDto.setUsername(trainer.getUser().getUsername());
        trainerDto.setFirstName(trainer.getUser().getFirstName());
        trainerDto.setLastName(trainer.getUser().getLastName());
        trainerDto.setSpecialization(trainer.getSpecialization());
        return trainerDto;
    }

    public TrainingDto convertToTrainingDto(Training training) {
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainingName(training.getName());
        trainingDto.setDate(training.getDate());
        trainingDto.setTrainingType(training.getTrainingType());
        trainingDto.setDuration(training.getDuration());
        if (training.getTrainer() != null) {
            trainingDto.setTrainerName(training.getTrainer().getUser().getFirstName());
        }
        return trainingDto;
    }

    public boolean validateUpdateRequest(UpdateRequestDto updateRequestDto) {
        if (updateRequestDto == null) {
            logger.error("Update request is null.");
            throw new InvalidInputException("Update request cannot be null");
        }

        validateStringField(updateRequestDto.getUsername(), "Username");
        if (usernameExists(userRepository.findAll(), updateRequestDto.getUsername())) {
            logger.error("Username already exists. Username: {}", updateRequestDto.getUsername());
            throw new InvalidInputException("Username already exists");
        }

        validateStringField(updateRequestDto.getFirstName(), "First Name");
        validateStringField(updateRequestDto.getLastName(), "Last Name");

        return true;
    }

    public void validateStringField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " is required and cannot be empty");
        }
    }
}
