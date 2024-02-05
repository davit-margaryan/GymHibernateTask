package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainerSummary;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TrainerService;
import com.example.gymhibernatetask.util.TransactionLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/trainers")
@Api(tags = "Trainer Management")
public class TrainerController {

    private static final String TRANSACTION_INFO = "Received request to fetch trainee profile";

    private final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TransactionLogger transactionLogger;
    private final TrainerService trainerService;
    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    public TrainerController(TransactionLogger transactionLogger,
                             TrainerService trainerService, JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.transactionLogger = transactionLogger;
        this.trainerService = trainerService;
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
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

    @GetMapping("/summary/{username}")
    public ResponseEntity<TrainerSummary> getTrainerSummary(@PathVariable String username) throws JMSException {
        Connection connection = null;
        Session session = null;
        MessageConsumer responseConsumer = null;

        try {
            String correlationId = UUID.randomUUID().toString();
            connection = Objects.requireNonNull(jmsTemplate.getConnectionFactory()).createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination responseQueue = session.createTemporaryQueue();

            logger.info("About to send message for user {}", username);
            jmsTemplate.convertAndSend("getTrainerSummary.queue", username, message -> {
                message.setJMSCorrelationID(correlationId);
                message.setJMSReplyTo(responseQueue);
                return message;
            });
            logger.info("Message sent for user {}", username);

            connection.start();
            String selector = String.format("JMSCorrelationID = '%s'", correlationId);
            responseConsumer = session.createConsumer(responseQueue, selector);

            logger.info("About to receive message");
            Message responseMessage = responseConsumer.receive(5000);
            logger.info("Message received");

            if (responseMessage != null) {
                if (responseMessage instanceof TextMessage textMessage) {
                    try {
                        TrainerSummary trainerSummary = objectMapper.readValue(textMessage.getText(), TrainerSummary.class);
                        logger.info("Successfully parsed trainer summary");
                        return ResponseEntity.ok(trainerSummary);
                    } catch (IOException e) {
                        logger.error("Error while parsing responseMessage to TrainerSummary: ", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    logger.error("Response is not a TextMessage: {}", responseMessage);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                logger.info("No response received within timeout period");
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }
        } finally {
            if (responseConsumer != null) {
                responseConsumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
