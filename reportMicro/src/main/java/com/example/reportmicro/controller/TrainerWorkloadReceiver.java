package com.example.reportmicro.controller;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.service.TrainerWorkloadService;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public void getTrainerSummary(Message message, @Payload String username,
                                  @Headers Map<String, Object> headers) throws JMSException {
        String correlationId = (String) headers.get("jms_correlationId");
        Destination replyTo = message.getJMSReplyTo();
        TrainerSummary summary = service.calculateMonthlySummary(username, correlationId);
        LOG.info("Sending back summary {} with correlationId {}", summary, correlationId);

        // manually send the response message to the reply queue
        this.jmsTemplate.convertAndSend(replyTo, summary, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message msg) throws JMSException {
                msg.setJMSCorrelationID(correlationId);
                return msg;
            }
        });
    }
}