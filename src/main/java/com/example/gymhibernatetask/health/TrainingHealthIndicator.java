package com.example.gymhibernatetask.health;

import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

@Component
public class TrainingHealthIndicator implements HealthIndicator {

    private static final String BASE_ENDPOINT = "http://localhost:8083/trainings";

    private final RestTemplate restTemplate;

    public TrainingHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            String username = "trainer1";
            String password = "password1";

            CreateTrainingRequestDto requestDto = createTrainingRequestDto();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<CreateTrainingRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    BASE_ENDPOINT + "?username={username}&password={password}",
                    HttpMethod.POST,
                    requestEntity,
                    Void.class,
                    username,
                    password);

            return HealthIndicatorUtils.createHealth(BASE_ENDPOINT, response);
        } catch (Exception e) {
            return HealthIndicatorUtils.createHealthWithError(BASE_ENDPOINT, e);
        }
    }


    private CreateTrainingRequestDto createTrainingRequestDto() {
        CreateTrainingRequestDto requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername("trainee1");
        requestDto.setTrainerUsername("trainer1");
        requestDto.setTrainingName("Cardio");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.MAY, 1);
        requestDto.setDate(calendar.getTime());
        requestDto.setDuration(60);
        return requestDto;
    }
}
