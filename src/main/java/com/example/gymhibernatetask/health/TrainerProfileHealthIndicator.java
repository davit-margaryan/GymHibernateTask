package com.example.gymhibernatetask.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TrainerProfileHealthIndicator implements HealthIndicator {

    private static final String BASE_ENDPOINT = "http://localhost:8083/trainers/";

    private final RestTemplate restTemplate;

    public TrainerProfileHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            String searchUsername = "trainer2";
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_ENDPOINT + "{searchUsername}?username={username}&password={password}",
                    String.class, searchUsername, Constants.USERNAME_PARAM, Constants.PASSWORD_PARAM);

            return HealthIndicatorUtils.createHealth(BASE_ENDPOINT, response);
        } catch (Exception e) {
            return HealthIndicatorUtils.createHealthWithError(BASE_ENDPOINT, e);
        }
    }
}
