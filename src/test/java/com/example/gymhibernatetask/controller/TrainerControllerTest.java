package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainerSummary;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.service.TrainerService;
import com.example.gymhibernatetask.util.TransactionLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private TrainerService trainerService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainerProfile() {
        String searchUsername = "searchUser";

        when(trainerService.selectTrainerProfile(searchUsername)).thenReturn(mock(TrainerResponseDto.class));

        ResponseEntity<TrainerResponseDto> response = trainerController.getTrainerProfile(searchUsername);

        verify(trainerService).selectTrainerProfile(searchUsername);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainer() {
        String username = "testUser";
        UpdateTrainerRequestDto updateRequestDto = mock(UpdateTrainerRequestDto.class);

        when(trainerService.updateTrainer(username, updateRequestDto)).thenReturn(mock(TrainerResponseDto.class));

        ResponseEntity<TrainerResponseDto> response = trainerController.updateTrainer(username, updateRequestDto);

        verify(trainerService).updateTrainer(username, updateRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTrainerTrainingsList() {
        String trainerUsername = "testUser";
        Date periodFrom = mock(Date.class);
        Date periodTo = mock(Date.class);
        String traineeName = "John";
        TrainingType trainingType = mock(TrainingType.class);

        when(trainingType.getTrainingTypeName()).thenReturn("Cardio");
        when(trainerService.getTrainerTrainingsList(trainerUsername, periodFrom, periodTo, traineeName, trainingType))
                .thenReturn(Arrays.asList(mock(TrainingDto.class)));

        ResponseEntity<List<TrainingDto>> response = trainerController.getTrainerTrainingsList(
                trainerUsername, periodFrom, periodTo, traineeName, trainingType);

        verify(trainerService).getTrainerTrainingsList(
                trainerUsername, periodFrom, periodTo, traineeName, trainingType);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeActiveStatus() {
        String username = "testUser";
        boolean activeStatus = true;

        ResponseEntity<Void> response = trainerController.changeActiveStatus(username, activeStatus);

        verify(trainerService).changeActiveStatus(username, activeStatus);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetTrainerSummary() throws JMSException, IOException {
        String username = "username";

        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);
        TextMessage message = mock(TextMessage.class);
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        TemporaryQueue tempQueue = mock(TemporaryQueue.class);

        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        when(session.createTemporaryQueue()).thenReturn(tempQueue);
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(jmsTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(session.createConsumer(any(Destination.class), anyString())).thenReturn(messageConsumer);
        when(messageConsumer.receive(anyLong())).thenReturn(message);
        when(message.getText()).thenReturn("{}");

        TrainerSummary summary = mock(TrainerSummary.class);
        when(objectMapper.readValue(anyString(), eq(TrainerSummary.class))).thenReturn(summary);

        ResponseEntity<TrainerSummary> result = trainerController.getTrainerSummary(username);

        assertNotNull(result.getBody());
        assertEquals(summary, result.getBody());
    }
}
