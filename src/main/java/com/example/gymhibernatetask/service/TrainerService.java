package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TrainerService {
    TrainerResponseDto selectTrainerProfile(String searchUsername);

    TrainerResponseDto updateTrainer(String username, UpdateTrainerRequestDto updateRequestDto);

    void changeActiveStatus(String username, boolean activeStatus);

    List<TrainingDto> getTrainerTrainingsList(String trainerUsername,
                                              Date periodFrom, Date periodTo,
                                              String traineeName, TrainingType trainingType);
}
