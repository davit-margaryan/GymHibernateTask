package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.TraineeResponseDto;
import com.example.gymhibernatetask.dto.TrainerListResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTraineeRequestDto;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.service.TraineeService;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;
import com.example.gymhibernatetask.util.TransactionLogger;
import io.swagger.annotations.Api;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.transaction.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trainees")
@Api(tags = "Trainee Management")
public class TraineeController {

    private static final String TRANSACTION_INFO = "Received request to fetch trainee profile";
    private final TransactionLogger transactionLogger;
    private final TraineeService traineeService;
    private final TraineeRepository traineeRepository;
    private final JmsTemplate jmsTemplate;

    public TraineeController(TransactionLogger transactionLogger,
                             TraineeService traineeService,
                             TraineeRepository traineeRepository, JmsTemplate jmsTemplate) {
        this.transactionLogger = transactionLogger;
        this.traineeService = traineeService;
        this.traineeRepository = traineeRepository;
        this.jmsTemplate = jmsTemplate;
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteTrainee(@RequestParam String deleteUsername) {
        UUID correlationId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);
        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername(deleteUsername);
        if (traineeByUserUsername.isEmpty()) {
            throw new RuntimeException("Trainee not found");
        }
        List<Trainer> trainers = traineeByUserUsername.get().getTrainers();
        if (trainers != null) {
            for (Trainer trainer : trainers) {
                System.out.println(trainer.getUser().getUsername());
                TrainerWorkloadRequest trainerWorkload = new TrainerWorkloadRequest();
                trainerWorkload.setUsername(trainer.getUser().getUsername());
                trainerWorkload.setActionType("DELETE");
                jmsTemplate.convertAndSend(
                        "manageTrainerWorkload.queue",
                        trainerWorkload,
                        new MessagePostProcessor() {
                            @Override
                            public Message postProcessMessage(@NonNull Message message) throws JMSException {
                                message.setStringProperty("correlationId", String.valueOf(correlationId));
                                return message;
                            }
                        });

            }
        }
        traineeService.deleteTrainee(deleteUsername);
        transactionLogger.logTransactionSuccess("Trainee deleted successfully", correlationId, deleteUsername);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{searchUsername}")
    public ResponseEntity<TraineeResponseDto> getTraineeProfile(@PathVariable String searchUsername) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TraineeResponseDto trainee = traineeService.selectTraineeProfile(searchUsername);
        transactionLogger.logTransactionSuccess("Trainee profile fetched successfully", transactionId, searchUsername);

        return ResponseEntity.ok(trainee);
    }

    @PutMapping
    public ResponseEntity<TraineeResponseDto> updateTrainee(@RequestParam String username, @RequestBody UpdateTraineeRequestDto updateRequestDto) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        TraineeResponseDto updatedTrainee = traineeService.updateTrainee(username, updateRequestDto);
        transactionLogger.logTransactionSuccess("Trainee profile updated successfully", transactionId, username);

        return ResponseEntity.ok(updatedTrainee);
    }

    @GetMapping("/{traineeUsername}/trainings")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainingsList(@PathVariable String traineeUsername,
                                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodFrom,
                                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodTo,
                                                                     @RequestParam(required = false) String trainerFirstName,
                                                                     @RequestParam(required = false) TrainingType trainingType) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainingDto> trainingsList = traineeService.getTraineeTrainingsList(traineeUsername, periodFrom, periodTo, trainerFirstName, trainingType);
        transactionLogger.logTransactionSuccess("Trainee profile got Trainings list", transactionId, traineeUsername);

        return ResponseEntity.ok(trainingsList);
    }

    @GetMapping("/{traineeUsername}/available-trainers")
    public ResponseEntity<List<TrainerListResponseDto>> getAvailableTrainersForTrainee(@PathVariable String traineeUsername) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainerListResponseDto> availableTrainers = traineeService.getAvailableTrainersForTrainee(traineeUsername);
        transactionLogger.logTransactionSuccess("Trainee profile got Available Trainers successfully", transactionId, traineeUsername);

        return ResponseEntity.ok(availableTrainers);
    }

    @PutMapping("/update-trainers")
    public ResponseEntity<List<TrainerListResponseDto>> updateTraineeTrainers(@RequestParam String username, @RequestBody List<Trainer> trainers) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        List<TrainerListResponseDto> updatedTrainers = traineeService.updateTraineeTrainers(username, trainers);
        transactionLogger.logTransactionSuccess("Trainee profile updated Trainers successfully", transactionId, username);

        return ResponseEntity.ok(updatedTrainers);
    }

    @PatchMapping("/change-active-status")
    public ResponseEntity<Void> changeActiveStatus(@RequestParam String username, @RequestParam boolean activeStatus) {
        UUID transactionId = transactionLogger.logTransactionRequest(TRANSACTION_INFO);

        traineeService.changeActiveStatus(username, activeStatus);
        transactionLogger.logTransactionSuccess("Trainee profile status changes successfully", transactionId, username);

        return ResponseEntity.noContent().build();
    }
}
