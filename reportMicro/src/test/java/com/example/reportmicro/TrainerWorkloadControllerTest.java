package com.example.reportmicro;

import com.example.reportmicro.controller.TrainerWorkloadController;
import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.service.TrainerWorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadControllerTest {

    @Mock
    private MockMvc mockMvc;

    @Mock
    private TrainerWorkloadService service;

    @InjectMocks
    private TrainerWorkloadController controller;

    @Test
    void manageTrainerWorkload() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType("ADD");
        request.setUsername("testuser");
        request.setFirstName("test");
        request.setLastName("user");
        request.setActive(true);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/trainerWorkload")
                        .header("X-Correlation-ID", "someId")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerSummary() {
        String username = "testUser";
        String correlationId = "123";
        TrainerSummary trainerSummary = new TrainerSummary();

        when(service.calculateMonthlySummary(username, correlationId)).thenReturn(trainerSummary);

        ResponseEntity<TrainerSummary> response = controller.getTrainerSummary(username, correlationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerSummary, response.getBody());
    }
}