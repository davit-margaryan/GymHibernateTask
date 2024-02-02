package com.example.gymhibernatetask.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DbHealthIndicatorTest {

    @InjectMocks
    private DbHealthIndicator dbHealthIndicator;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testHealth_up() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        Health result = dbHealthIndicator.health();

        assertEquals(Status.UP, result.getStatus());
        assertEquals("Database is available", result.getDetails().get(Constants.STATUS));
    }

    @Test
    public void testHealth_down() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException());

        Health result = dbHealthIndicator.health();

        assertEquals(Status.DOWN, result.getStatus());
        assertEquals("Database is not reachable", result.getDetails().get(Constants.STATUS));
    }

    @Test
    public void testHealth_down_empty() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(null);

        Health result = dbHealthIndicator.health();

        assertEquals(Status.DOWN, result.getStatus());
        assertEquals("Database is not reachable", result.getDetails().get(Constants.STATUS));
    }
}