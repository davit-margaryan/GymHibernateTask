package com.example.reportmicro;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerWorkload;
import com.example.reportmicro.repo.TrainerWorkloadRepository;
import com.example.reportmicro.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}