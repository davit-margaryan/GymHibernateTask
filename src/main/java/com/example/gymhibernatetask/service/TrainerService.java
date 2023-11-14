package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateResponseDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TrainerService {

    CreateResponseDto createTrainer(CreateTrainerRequestDto trainerRequestDto);

    Trainer selectTrainerProfile(String username, String password, String searchUsername);

    Trainer updateTrainer(String username, String password, UpdateTrainerRequestDto updateRequestDto);

    void changeActiveStatus(String username, String password, boolean activeStatus);

    public List<TrainingDto> getTrainerTrainingsList(String trainerUsername, String password,
                                                     Date periodFrom, Date periodTo,
                                                     String traineeName, TrainingType trainingType);
}
