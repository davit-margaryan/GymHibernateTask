package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainings")
@Api(tags = "Training Management")
public class TrainingController {

    private final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Void> createTraining(@RequestBody CreateTrainingRequestDto requestDto) {
        trainingService.createTraining(requestDto);

        logger.info("Training created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
