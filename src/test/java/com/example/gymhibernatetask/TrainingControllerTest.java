package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainingController;
import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.service.TrainingService;
import com.example.gymhibernatetask.dto.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @InjectMocks
    TrainingController trainingController;

    @Mock
    TrainingService trainingService;

    @Mock
    JmsTemplate jmsTemplate;

    @Captor
    ArgumentCaptor<MessagePostProcessor> postProcessorCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateTraining() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CreateTrainingRequestDto newTraining = new CreateTrainingRequestDto();
        TrainerWorkloadRequest trainerWorkload = new TrainerWorkloadRequest();

        when(trainingService.createTraining(any())).thenReturn(trainerWorkload);

        trainingController.createTraining(newTraining);

        verify(trainingService, times(1)).createTraining(newTraining);
        verify(jmsTemplate, times(1)).convertAndSend(eq("manageTrainerWorkload.queue"), eq(trainerWorkload), postProcessorCaptor.capture());

        assertEquals(201, trainingController.createTraining(newTraining).getStatusCodeValue());
    }

}