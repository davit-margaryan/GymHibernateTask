package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkload;
import com.example.gymhibernatetask.trainerWorkload.TrainerWorkloadClient;
import io.swagger.annotations.Api;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/trainings")
@Api(tags = "Training Management")
public class TrainingController {

    private final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TrainingService trainingService;
    private final TrainerWorkloadClient workloadClient;

    public TrainingController(TrainingService trainingService, TrainerWorkloadClient workloadClient) {
        this.trainingService = trainingService;
        this.workloadClient = workloadClient;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> createTraining(@RequestBody CreateTrainingRequestDto requestDto) {
        TrainerWorkload trainerWorkload = trainingService.createTraining(requestDto);

        String correlationId = UUID.randomUUID().toString();

        workloadClient.manageTrainerWorkload(trainerWorkload, correlationId);

        logger.info("CorrelationId {}: Training created successfully", correlationId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
