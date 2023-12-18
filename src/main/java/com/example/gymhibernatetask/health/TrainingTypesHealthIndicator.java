package com.example.gymhibernatetask.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private static final String BASE_ENDPOINT = "http://localhost:8083/training-types";

    private final RestTemplate restTemplate;

    public TrainingTypesHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(BASE_ENDPOINT, List.class);

            return HealthIndicatorUtils.createHealth(BASE_ENDPOINT, response);
        } catch (Exception e) {
            return HealthIndicatorUtils.createHealthWithError(BASE_ENDPOINT, e);
        }
    }
}
