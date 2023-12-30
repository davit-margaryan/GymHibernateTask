package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.TrainingTypeController;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.repository.TrainingTypeRepository;
import com.example.gymhibernatetask.service.TrainingTypeService;
import com.example.gymhibernatetask.util.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingTypeControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainingTypes() {
        UUID transactionId = UUID.randomUUID();

        when(transactionLogger.logTransactionRequest("Received request to fetch all training types")).thenReturn(transactionId);

        List<TrainingType> trainingTypes = List.of(new TrainingType(), new TrainingType());
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);

        ResponseEntity<List<TrainingType>> response = trainingTypeController.getAllTrainingTypes();

        verify(transactionLogger).logTransactionRequest("Received request to fetch all training types");
        verify(transactionLogger).logTransactionMessage("All training types fetched successfully", transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
