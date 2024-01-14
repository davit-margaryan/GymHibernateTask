package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkload;

public interface TrainingService {

    TrainerWorkload createTraining(CreateTrainingRequestDto requestDto);
}
