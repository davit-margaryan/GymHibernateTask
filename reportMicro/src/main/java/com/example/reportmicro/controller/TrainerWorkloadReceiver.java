package com.example.reportmicro.controller;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.service.TrainerWorkloadService;
import jakarta.jms.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadReceiver.class);

    @Autowired
    private TrainerWorkloadService service;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "manageTrainerWorkload.queue")
    public void manageTrainerWorkload(@Payload TrainerWorkloadRequest request,
                                      @Header("correlationId") String correlationId) {
        service.manageTrainerWorkload(request, correlationId);
    }

    @JmsListener(destination = "getTrainerSummary.queue")
    public void getTrainerSummary(@Payload String username,
                                  @Header(name = JmsHeaders.CORRELATION_ID) String correlationId,
                                  @Header(name = JmsHeaders.REPLY_TO) Destination replyTo) {

        TrainerSummary summary = service.calculateMonthlySummary(username, correlationId);
        LOG.info("Sending back summary {} with correlationId {}", summary, correlationId);

        jmsTemplate.convertAndSend(replyTo, summary, message -> {
            message.setJMSCorrelationID(correlationId);
            return message;
        });
    }
}