package com.example.gymhibernatetask.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthIndicatorUtilsTests {

    @Test
    public void testCreateHealth_successful() {
        ResponseEntity<String> response = new ResponseEntity<>("OK", HttpStatus.OK);
        Health health = HealthIndicatorUtils.createHealth("/test", response);

        assertEquals(Status.UP, health.getStatus());
        assertEquals("/test", health.getDetails().get(Constants.ENDPOINT));
        assertEquals(Constants.STATUS_UP, health.getDetails().get(Constants.STATUS));
        assertEquals("200", health.getDetails().get(Constants.HTTP_STATUS_CODE));
    }

    @Test
    public void testCreateHealth_error() {
        ResponseEntity<String> response = new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        Health health = HealthIndicatorUtils.createHealth("/test", response);

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("/test", health.getDetails().get(Constants.ENDPOINT));
        assertEquals(Constants.STATUS_DOWN, health.getDetails().get(Constants.STATUS));
        assertEquals("404", health.getDetails().get(Constants.HTTP_STATUS_CODE));
    }

    @Test
    public void testCreateHealthWithError() {
        Exception exception = new Exception("Error message");
        Health health = HealthIndicatorUtils.createHealthWithError("/test", exception);

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("/test", health.getDetails().get(Constants.ENDPOINT));
        assertEquals(Constants.STATUS_DOWN, health.getDetails().get(Constants.STATUS));
        assertEquals("Error: Error message", health.getDetails().get(Constants.HTTP_STATUS_CODE));
    }
}