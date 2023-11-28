package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.util.TransactionLogger;
import io.swagger.annotations.Api;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainees")
@Api(tags = "Trainee Management")
public class TraineeController {

    private static final String TRANSACTION_INFO = "Received request to fetch trainee profile";
    private final TransactionLogger transactionLogger;
    private final TraineeService traineeService;

    public TraineeController(TransactionLogger transactionLogger, TraineeService traineeService) {
        this.transactionLogger = transactionLogger;
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<CreateResponseDto> createTrainee(
            @RequestBody CreateTraineeRequestDto traineeRequestDto) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        CreateResponseDto responseDto = traineeService.createTrainee(traineeRequestDto);
        transactionLogger.logTransactionSuccess(
                "Trainee created successfully", transactionId, responseDto.getUsername());

        return ResponseEntity.status(201).body(responseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTrainee(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String deleteUsername) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        traineeService.deleteTrainee(username, password, deleteUsername);
        transactionLogger.logTransactionSuccess(
                "Trainee deleted successfully", transactionId, deleteUsername);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{searchUsername}")
    public ResponseEntity<TraineeResponseDto> getTraineeProfile(
            @RequestParam String username,
            @RequestParam String password,
            @PathVariable String searchUsername) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TraineeResponseDto trainee = traineeService.selectTraineeProfile(username, password, searchUsername);
        transactionLogger.logTransactionSuccess(
                "Trainee profile fetched successfully", transactionId, searchUsername);

        return ResponseEntity.ok(trainee);
    }

    @PutMapping
    public ResponseEntity<TraineeResponseDto> updateTrainee(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody UpdateTraineeRequestDto updateRequestDto) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TraineeResponseDto updatedTrainee = traineeService.updateTrainee(username, password, updateRequestDto);

        transactionLogger.logTransactionSuccess(
                "Trainee profile updated successfully", transactionId, username);

        return ResponseEntity.ok(updatedTrainee);
    }

    @GetMapping("/{traineeUsername}/trainings")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainingsList(
            @PathVariable String traineeUsername,
            @RequestParam String password,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodFrom,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodTo,
            @RequestParam(required = false) String trainerFirstName,
            @RequestParam(required = false) TrainingType trainingType) {

        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainingDto> trainingsList = traineeService.getTraineeTrainingsList(
                traineeUsername, password, periodFrom, periodTo, trainerFirstName, trainingType);

        transactionLogger.logTransactionSuccess(
                "Trainee profile got Trainings list", transactionId, traineeUsername);

        return ResponseEntity.ok(trainingsList);
    }

    @GetMapping("/{traineeUsername}/available-trainers")
    public ResponseEntity<List<TrainerListResponseDto>> getAvailableTrainersForTrainee(
            @RequestParam String username,
            @RequestParam String password,
            @PathVariable String traineeUsername) {

        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainerListResponseDto> availableTrainers = traineeService.getAvailableTrainersForTrainee(
                username, password, traineeUsername);

        transactionLogger.logTransactionSuccess(
                "Trainee profile got Available Trainers successfully",
                transactionId, username);

        return ResponseEntity.ok(availableTrainers);
    }

    @PutMapping("/update-trainers")
    public ResponseEntity<List<TrainerListResponseDto>> updateTraineeTrainers(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody List<Trainer> trainers) {

        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainerListResponseDto> updatedTrainers = traineeService
                .updateTraineeTrainers(username, password, trainers);

        transactionLogger.logTransactionSuccess(
                "Trainee profile updated Trainers successfully",
                transactionId, username);

        return ResponseEntity.ok(updatedTrainers);
    }

    @PatchMapping("/change-active-status")
    public ResponseEntity<Void> changeActiveStatus(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam boolean activeStatus) {

        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        traineeService.changeActiveStatus(username, password, activeStatus);

        transactionLogger.logTransactionSuccess(
                "Trainee profile status changes successfully",
                transactionId, username);

        return ResponseEntity.noContent().build();
    }
}
