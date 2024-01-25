package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;

public interface TrainingService {

    TrainerWorkloadRequest createTraining(CreateTrainingRequestDto requestDto);
}
