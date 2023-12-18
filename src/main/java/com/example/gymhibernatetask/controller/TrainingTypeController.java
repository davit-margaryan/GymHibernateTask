package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TrainingTypeService;
import com.example.gymhibernatetask.util.TransactionLogger;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/training-types")
@Api(tags = "Training Type Management")
public class TrainingTypeController {

    private final TransactionLogger transactionLogger;
    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TransactionLogger transactionLogger, TrainingTypeService trainingTypeService) {
        this.transactionLogger = transactionLogger;
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        UUID transactionId = transactionLogger.logTransactionRequest("Received request to fetch all training types");

        List<TrainingType> trainingTypes = trainingTypeService.getAll();
        transactionLogger.logTransactionMessage("All training types fetched successfully", transactionId);

        return ResponseEntity.ok(trainingTypes);
    }
}
