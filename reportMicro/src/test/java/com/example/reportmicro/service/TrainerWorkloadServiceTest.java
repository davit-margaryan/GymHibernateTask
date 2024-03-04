package com.example.reportmicro.service;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.model.TrainerWorkload;
import com.example.reportmicro.repo.TrainerSummaryRepository;
import com.example.reportmicro.repo.TrainerWorkloadRepository;
import com.example.reportmicro.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @Mock
    private TrainerSummaryRepository trainerSummaryRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    @Mock
    TrainerWorkloadRequest request;

    @Mock
    TrainerWorkload workload;

    @Mock
    TrainerSummary summary;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addWorkloadTest() {
        when(request.getActionType()).thenReturn("ADD");
        when(request.getUsername()).thenReturn("testUser");
        when(request.getFirstName()).thenReturn("testFirstName");
        when(request.getLastName()).thenReturn("testLastName");
        when(request.isActive()).thenReturn(true);
        when(request.getTrainingDate()).thenReturn(new Date());
        when(request.getTrainingDuration()).thenReturn(30);
        when(request.getTraineeUsername()).thenReturn("testTrainee");

        TrainerWorkload expectedWorkload = getTrainerWorkload(request);

        when(repository.saveAndFlush(any(TrainerWorkload.class))).thenReturn(expectedWorkload);
        when(repository.getAllByUsername(anyString())).thenReturn(Collections.singletonList(expectedWorkload));
        when(trainerSummaryRepository.findByUsername(anyString())).thenReturn(Optional.of(summary));

        service.manageTrainerWorkload(request, "anyString");

        verify(repository, times(1)).saveAndFlush(expectedWorkload);
        verify(repository, times(1)).getAllByUsername(request.getUsername());
        verify(trainerSummaryRepository, times(1)).findByUsername(request.getUsername());
        verify(trainerSummaryRepository, times(1)).save(any(TrainerSummary.class));

        assertEquals(expectedWorkload, repository.saveAndFlush(expectedWorkload));
    }

    private static TrainerWorkload getTrainerWorkload(TrainerWorkloadRequest request) {
        TrainerWorkload expectedWorkload = new TrainerWorkload();
        expectedWorkload.setUsername(request.getUsername());
        expectedWorkload.setFirstName(request.getFirstName());
        expectedWorkload.setLastName(request.getLastName());
        expectedWorkload.setActive(request.isActive());
        expectedWorkload.setDate(request.getTrainingDate());
        expectedWorkload.setDuration(request.getTrainingDuration());
        expectedWorkload.setTraineeUsername(request.getTraineeUsername());
        return expectedWorkload;
    }

    @Test
    public void manageTrainerWorkload_Delete_Test() {
        when(request.getActionType()).thenReturn("DELETE");
        when(request.getTraineeUsername()).thenReturn("anyString");
        when(repository.existsByTraineeUsername(anyString())).thenReturn(true);

        service.manageTrainerWorkload(request, "anyString");

        verify(repository, times(1)).deleteAllByTraineeUsername(anyString());
    }

    @Test
    public void calculateMonthlySummaryTest() {
        when(workload.getDate()).thenReturn(new Date());
        when(repository.getAllByUsername(anyString())).thenReturn(Collections.singletonList(workload));
        when(trainerSummaryRepository.findByUsername(anyString())).thenReturn(Optional.of(summary));

        TrainerSummary result = service.calculateSummary("anyString", "anyString");  // <-- Here is the change

        verify(trainerSummaryRepository, times(1)).save(any(TrainerSummary.class));

        assertEquals(summary, result);
    }
}