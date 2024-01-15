package com.example.reportmicro;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.model.TrainerWorkload;
import com.example.reportmicro.repo.TrainerWorkloadRepository;
import com.example.reportmicro.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    @Mock
    private TrainerWorkloadRepository repository;

    @Test
    void manageTrainerWorkload_ADD() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType("ADD");
        request.setUsername("testuser");
        request.setFirstName("test");
        request.setLastName("user");
        request.setActive(true);

        when(repository.existsByUsername(request.getUsername())).thenReturn(false);

        trainerWorkloadService.manageTrainerWorkload(request, "someID");

        verify(repository, times(1)).save(any(TrainerWorkload.class));
    }

    @Test
    void manageTrainerWorkload_DELETE() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType("DELETE");
        request.setUsername("testuser");

        when(repository.existsByUsername(request.getUsername())).thenReturn(true);

        trainerWorkloadService.manageTrainerWorkload(request, "someID");

        verify(repository, times(1)).deleteByUsername(request.getUsername());
    }

    @Test
    void testCalculateMonthlySummary() {
        String username = "testUser";
        String correlationId = "123";

        List<TrainerWorkload> trainerWorkloads = new ArrayList<>();
        TrainerWorkload workload = new TrainerWorkload();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        workload.setDate(cal.getTime());
        workload.setDuration(120);
        trainerWorkloads.add(workload);

        when(repository.getAllByUsername(username)).thenReturn(trainerWorkloads);

        TrainerSummary summary = trainerWorkloadService.calculateMonthlySummary(username, correlationId);

        assertEquals(username, summary.getUsername());
        assertEquals(List.of(cal.get(Calendar.YEAR)), summary.getYears());
        assertEquals((Integer) 120, summary.getMonthlySummary().get(String.valueOf(cal.get(Calendar.YEAR))).get(String.valueOf(cal.get(Calendar.MONTH) + 1)));
    }
}