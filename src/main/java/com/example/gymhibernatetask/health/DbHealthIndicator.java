package com.example.gymhibernatetask.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DbHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            boolean isDatabaseUp = checkDatabaseHealth();

            if (isDatabaseUp) {
                return Health.up()
                        .withDetail(Constants.STATUS, "Database is available")
                        .build();
            } else {
                return Health.down()
                        .withDetail(Constants.STATUS, "Database is not reachable")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail(Constants.STATUS, "Exception during health check")
                    .build();
        }
    }

    private boolean checkDatabaseHealth() {
        try {
            Integer rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trainee", Integer.class);

            Objects.requireNonNull(rowCount, "RowCount is null");

            return rowCount >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
