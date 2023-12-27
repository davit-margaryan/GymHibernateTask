package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TrainerService;
import com.example.gymhibernatetask.util.TransactionLogger;
import io.swagger.annotations.Api;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainers")
@Api(tags = "Trainer Management")
public class TrainerController {

    private static final String TRANSACTION_INFO = "Received request to fetch trainee profile";
    private final TransactionLogger transactionLogger;
    private final TrainerService trainerService;

    public TrainerController(TransactionLogger transactionLogger, TrainerService trainerService) {
        this.transactionLogger = transactionLogger;
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<CreateResponseDto> createTrainer(@RequestBody CreateTrainerRequestDto trainerRequestDto) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        CreateResponseDto responseDto = trainerService.createTrainer(trainerRequestDto);
        transactionLogger.logTransactionSuccess(
                "Trainer profile fetched successfully", transactionId, responseDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{searchUsername}")
    public ResponseEntity<TrainerResponseDto> getTrainerProfile(@PathVariable String searchUsername) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TrainerResponseDto trainer = trainerService.selectTrainerProfile(searchUsername);
        transactionLogger.logTransactionSuccess(
                "Trainer profile fetched successfully", transactionId, searchUsername);

        return ResponseEntity.ok(trainer);
    }

    @PutMapping
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @RequestParam String username,
            @RequestBody UpdateTrainerRequestDto updateRequestDto) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TrainerResponseDto updatedTrainer = trainerService.updateTrainer(username, updateRequestDto);
        transactionLogger.logTransactionSuccess("Trainer profile updated successfully", transactionId, username);

        return ResponseEntity.ok(updatedTrainer);
    }

    @GetMapping("/{trainerUsername}/trainings")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainingsList(
            @PathVariable String trainerUsername,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodFrom,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodTo,
            @RequestParam(required = false) String traineeName,
            @RequestParam(required = false) TrainingType trainingType) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainingDto> trainerTrainingsList = trainerService.getTrainerTrainingsList(
                trainerUsername, periodFrom, periodTo, traineeName, trainingType);
        transactionLogger.logTransactionSuccess("Trainer trainings list fetched successfully", transactionId, trainerUsername);

        return ResponseEntity.ok(trainerTrainingsList);
    }

    @PatchMapping("/change-active-status")
    public ResponseEntity<Void> changeActiveStatus(
            @RequestParam String username,
            @RequestParam boolean activeStatus) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        trainerService.changeActiveStatus(username, activeStatus);
        transactionLogger.logTransactionSuccess(
                "Trainer profile status changes successfully",
                transactionId, username);

        return ResponseEntity.noContent().build();
    }
}
