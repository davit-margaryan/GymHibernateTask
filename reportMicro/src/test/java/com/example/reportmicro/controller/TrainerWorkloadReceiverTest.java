package com.example.reportmicro.controller;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.service.TrainerWorkloadService;
import jakarta.jms.Destination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadReceiverTest {

    @Mock
    private TrainerWorkloadService service;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainerWorkloadReceiver receiver;

    private TrainerWorkloadRequest request;
    private TrainerSummary summary;

    @BeforeEach
    public void setUp() {
        request = new TrainerWorkloadRequest();
        summary = new TrainerSummary();
        lenient().when(service.calculateSummary(any(String.class), any(String.class))).thenReturn(summary);
    }

    @Test
    public void manageTrainerWorkloadTest() {
        String correlationId = "121212";
        receiver.manageTrainerWorkload(request, correlationId);
        verify(service, times(1)).manageTrainerWorkload(request, correlationId);
    }

    @Test
    public void getTrainerSummaryTest() {
        String username = "user123";
        String correlationId = "121212";
        Destination replyTo = Mockito.mock(Destination.class);
        when(service.calculateSummary(any(String.class), any(String.class))).thenReturn(summary);

        receiver.getTrainerSummary(username, correlationId, replyTo);

        verify(service, times(1)).calculateSummary(username, correlationId);
        verify(jmsTemplate, times(1)).convertAndSend(eq(replyTo), any(TrainerSummary.class), any());
    }
}