package com.example.gymhibernatetask.trainerWorkload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadClientFallback implements TrainerWorkloadClient {
    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadClientFallback.class);

    @Override
    public ResponseEntity<?> manageTrainerWorkload(TrainerWorkload request, String correlationId) {
        LOG.warn("Falling back for manageTrainerWorkload due to failure of the report-microservice. CorrelationId: {}", correlationId);
        throw new RuntimeException("The service fails");
    }
}