package com.example.reportmicro.service;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;

public interface TrainerWorkloadService {
    void manageTrainerWorkload(TrainerWorkloadRequest request, String correlationId);

    TrainerSummary calculateSummary(String trainerUsername, String correlationId);

}