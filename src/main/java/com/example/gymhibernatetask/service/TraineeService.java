package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TraineeService {

    CreateResponseDto createTrainee(CreateTraineeRequestDto traineeRequestDto);

    List<Trainee> getAllTrainee(String username, String password);

    void deleteTrainee(String username, String password, String deleteUsername);

    Trainee selectTraineeProfile(String username, String password, String searchUsername);

    Trainee updateTrainee(String username, String password, UpdateTraineeRequestDto updateRequestDto);

    void changeActiveStatus(String username, String password, boolean activeStatus);

    public List<TrainerListResponseDto> updateTraineeTrainers(String username, String password, List<Trainer> trainers);

    public List<TrainingDto> getTraineeTrainingsList(String traineeUsername, String password,
                                                     Date periodFrom, Date periodTo,
                                                     String trainerName, TrainingType trainingType);

    public List<TrainerListResponseDto> getAvailableTrainersForTrainee(String username, String password, String traineeUsername);
}
