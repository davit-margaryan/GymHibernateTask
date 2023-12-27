package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TraineeService {

    CreateResponseDto createTrainee(CreateTraineeRequestDto traineeRequestDto);

    void deleteTrainee(String deleteUsername);

    TraineeResponseDto selectTraineeProfile(String searchUsername);

    TraineeResponseDto updateTrainee(String username, UpdateTraineeRequestDto updateRequestDto);

    void changeActiveStatus(String username, boolean activeStatus);

    public List<TrainerListResponseDto> updateTraineeTrainers(String username, List<Trainer> trainers);

    public List<TrainingDto> getTraineeTrainingsList(String traineeUsername,
                                                     Date periodFrom, Date periodTo,
                                                     String trainerName, TrainingType trainingType);

    public List<TrainerListResponseDto> getAvailableTrainersForTrainee(String traineeUsername);
}
