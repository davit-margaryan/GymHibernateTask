package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;

public interface TrainingService {

    void createTraining(CreateTrainingRequestDto requestDto);
}
