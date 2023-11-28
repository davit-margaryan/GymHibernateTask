package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TrainerService {

    CreateResponseDto createTrainer(CreateTrainerRequestDto trainerRequestDto);

    TrainerResponseDto selectTrainerProfile(String username, String password, String searchUsername);

    TrainerResponseDto updateTrainer(String username, String password, UpdateTrainerRequestDto updateRequestDto);

    void changeActiveStatus(String username, String password, boolean activeStatus);

    List<TrainingDto> getTrainerTrainingsList(String trainerUsername, String password,
                                              Date periodFrom, Date periodTo,
                                              String traineeName, TrainingType trainingType);
}
