package com.example.gymhibernatetask.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HealthIndicatorUtils {

    private HealthIndicatorUtils() {
    }

    public static Health createHealth(String baseEndpoint, ResponseEntity<?> response) {
        String status = response.getStatusCode().is2xxSuccessful() ? Constants.STATUS_UP : Constants.STATUS_DOWN;
        return Health.status(status)
                .withDetail(Constants.ENDPOINT, baseEndpoint)
                .withDetail(Constants.STATUS, status)
                .withDetail(Constants.HTTP_STATUS_CODE, String.valueOf(response.getStatusCode().value()))
                .build();
    }

    public static Health createHealthWithError(String baseEndpoint, Exception e) {
        return Health.down()
                .withDetail(Constants.ENDPOINT, baseEndpoint)
                .withDetail(Constants.STATUS, Constants.STATUS_DOWN)
                .withDetail(Constants.HTTP_STATUS_CODE, "Error: " + e.getMessage())
                .build();
    }
}
