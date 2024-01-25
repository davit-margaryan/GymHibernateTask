package com.example.reportmicro;

import com.example.reportmicro.controller.TrainerWorkloadReceiver;
import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

public class TrainerWorkloadReceiverTest {

    @Mock
    private TrainerWorkloadService service;

    @InjectMocks
    private TrainerWorkloadReceiver underTest;

    @Mock
    private JmsTemplate jmsTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void manageTrainerWorkload() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        String correlationId = "123";
        doNothing().when(service).manageTrainerWorkload(request, correlationId);
        underTest.manageTrainerWorkload(request, correlationId);
        verify(service, times(1)).manageTrainerWorkload(request, correlationId);
    }

/*    @Test
    void getTrainerSummary() {
        String username = "username";
        TrainerSummary trainerSummary = new TrainerSummary();
        String correlationId = "123";
        Map<String, Object> headers = new HashMap<>();
        headers.put("jms_correlationId", correlationId);

        given(service.calculateMonthlySummary(username, correlationId)).willReturn(trainerSummary);
        TrainerSummary result = underTest.getTrainerSummary(username, headers);
        assertEquals(trainerSummary, result);
        verify(service, times(1)).calculateMonthlySummary(username, correlationId);
    }*/
}