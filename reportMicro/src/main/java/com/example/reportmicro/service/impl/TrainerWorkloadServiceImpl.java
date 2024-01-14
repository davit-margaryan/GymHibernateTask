package com.example.reportmicro.service.impl;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.model.TrainerWorkload;
import com.example.reportmicro.repo.TrainerWorkloadRepository;
import com.example.reportmicro.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);

    @Autowired
    private TrainerWorkloadRepository repository;

    @Override
    public void manageTrainerWorkload(TrainerWorkloadRequest request, String correlationId) {

        LOG.info("CorrelationId {}: Received request to manage trainer workload: {}", correlationId, request);

        switch (request.getActionType()) {
            case "ADD" -> {
                LOG.info("CorrelationId {}: Processing ADD action for trainer: {}", correlationId, request.getUsername());
                addWorkload(request, correlationId);
            }
            case "DELETE" -> {
                LOG.info("CorrelationId {}: Processing DELETE action for trainer: {}", correlationId, request.getUsername());
                deleteWorkload(request, correlationId);
            }
            default -> {
                LOG.error("CorrelationId {}: Unsupported action type: {}", correlationId, request.getActionType());
                throw new IllegalArgumentException("Unsupported action type");
            }
        }

        LOG.info("CorrelationId {}: Successfully processed request for trainer: {}", correlationId, request.getUsername());
    }

    private void addWorkload(TrainerWorkloadRequest request, String correlationId) {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setUsername(request.getUsername());
        workload.setFirstName(request.getFirstName());
        workload.setLastName(request.getLastName());
        workload.setActive(request.isActive());
        workload.setDate(request.getTrainingDate());
        workload.setDuration(request.getTrainingDuration());

        LOG.info("CorrelationId {}: Saving trainer workload for trainer: {}", correlationId, request.getUsername());
        repository.save(workload);
    }

    private void deleteWorkload(TrainerWorkloadRequest request, String correlationId) {

        if (!repository.existsByUsername(request.getUsername())) {
            LOG.error("CorrelationId {}: Trainer {} doesn't exist.", correlationId, request.getUsername());
            throw new IllegalStateException("Trainer with this username doesn't exist");
        }

        LOG.info("CorrelationId {}: Deleting trainer workload for trainer: {}", correlationId, request.getUsername());
        repository.deleteByUsername(request.getUsername());
    }
}