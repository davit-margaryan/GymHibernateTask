package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;
import io.swagger.annotations.Api;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.lang.NonNull;
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
    private final JmsTemplate jmsTemplate;

    public TrainingController(TrainingService trainingService, JmsTemplate jmsTemplate) {
        this.trainingService = trainingService;
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> createTraining(@RequestBody CreateTrainingRequestDto requestDto) {
        TrainerWorkloadRequest trainerWorkload = trainingService.createTraining(requestDto);

        String correlationId = UUID.randomUUID().toString();

        jmsTemplate.convertAndSend(
                "manageTrainerWorkload.queue",
                trainerWorkload,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(@NonNull Message message) throws JMSException {
                        message.setStringProperty("correlationId", correlationId);
                        return message;
                    }
                });

        logger.info("CorrelationId {}: Training created successfully", correlationId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
