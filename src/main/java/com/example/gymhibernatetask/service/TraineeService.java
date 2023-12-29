package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.TraineeResponseDto;
import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTraineeRequestDto;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;

import java.util.Date;
import java.util.List;

public interface TraineeService {

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
