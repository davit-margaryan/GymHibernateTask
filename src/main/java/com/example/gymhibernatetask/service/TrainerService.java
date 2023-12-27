package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TrainerService {

    CreateResponseDto createTrainer(CreateTrainerRequestDto trainerRequestDto);

    TrainerResponseDto selectTrainerProfile(String searchUsername);

    TrainerResponseDto updateTrainer(String username,UpdateTrainerRequestDto updateRequestDto);

    void changeActiveStatus(String username,boolean activeStatus);

    List<TrainingDto> getTrainerTrainingsList(String trainerUsername,
                                              Date periodFrom, Date periodTo,
                                              String traineeName, TrainingType trainingType);
}
