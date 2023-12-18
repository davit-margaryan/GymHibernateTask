package com.example.gymhibernatetask.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LoginHealthIndicator implements HealthIndicator {

    private static final String BASE_ENDPOINT = "http://localhost:8083/auth/login";

    private final RestTemplate restTemplate;

    public LoginHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_ENDPOINT + "?username=trainee1&password=password1",
                    String.class);

            return HealthIndicatorUtils.createHealth(BASE_ENDPOINT, response);
        } catch (Exception e) {
            return HealthIndicatorUtils.createHealthWithError(BASE_ENDPOINT, e);
        }
    }
}
