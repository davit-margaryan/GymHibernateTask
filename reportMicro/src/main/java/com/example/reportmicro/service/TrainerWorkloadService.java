package com.example.reportmicro.service;

import com.example.reportmicro.dto.TrainerWorkloadRequest;

public interface TrainerWorkloadService {
    void manageTrainerWorkload(TrainerWorkloadRequest request, String correlationId);
}