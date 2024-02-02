package com.example.gymhibernatetask.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypesHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TrainingTypesHealthIndicator trainingTypesHealthIndicator;

    @Test
    public void testHealth() {
        ResponseEntity<List> mockEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(List.class))).thenReturn(mockEntity);

        Health result = trainingTypesHealthIndicator.health();

        assertEquals(mockEntity.getStatusCode().is2xxSuccessful() ? Constants.STATUS_UP : Constants.STATUS_DOWN, result.getStatus().getCode());
        assertNotNull(result.getDetails().get(Constants.ENDPOINT));
        assertEquals(mockEntity.getStatusCode().is2xxSuccessful() ? Constants.STATUS_UP : Constants.STATUS_DOWN, result.getDetails().get(Constants.STATUS));
        assertEquals(String.valueOf(mockEntity.getStatusCodeValue()), result.getDetails().get(Constants.HTTP_STATUS_CODE));
    }

    @Test
    public void testHealth_withError() {
        when(restTemplate.getForEntity(anyString(), eq(List.class)))
                .thenThrow(new RuntimeException("Error"));

        Health result = trainingTypesHealthIndicator.health();

        assertEquals(Constants.STATUS_DOWN, result.getStatus().getCode());
        assertNotNull(result.getDetails().get(Constants.ENDPOINT));
        assertEquals(Constants.STATUS_DOWN, result.getDetails().get(Constants.STATUS));
        assertEquals("Error: Error", result.getDetails().get(Constants.HTTP_STATUS_CODE));
    }
}