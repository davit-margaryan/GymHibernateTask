package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginServiceImpl implements LoginService {

    private final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    private final UserRepository userRepository;
    private final Counter successfulLoginCounter;
    private final Counter failedLoginCounter;

    public LoginServiceImpl(UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.successfulLoginCounter = Counter.builder("login_successful")
                .description("Number of successful logins")
                .register(meterRegistry);

        this.failedLoginCounter = Counter.builder("login_failed")
                .description("Number of failed logins")
                .register(meterRegistry);
    }

    @Override
    public boolean login(String username, String password) {
        logger.info("Attempting login for username: {}", username);

        Optional<User> byUsername = userRepository.getByUsername(username);

        boolean isSuccess = byUsername.filter(user -> password.equals(user.getPassword())).isPresent();

        if (isSuccess) {
            logger.info("Login successful for username: {}", username);
            successfulLoginCounter.increment();
        } else {
            logger.warn("Login failed for username: {}", username);
            failedLoginCounter.increment();
        }

        return isSuccess;
    }

    @Override
    public boolean changeLogin(String username, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for username: {}", username);

        Optional<User> byUsername = userRepository.getByUsername(username);

        if (byUsername.isPresent() && byUsername.get().getPassword().equals(oldPassword) &&
                newPassword != null && !newPassword.trim().isEmpty()) {

            User user = byUsername.get();
            user.setPassword(newPassword);
            userRepository.save(user);

            logger.info("Password changed successfully for username: {}", username);
            return true;
        } else {
            logger.warn("Failed to change password for username: {}", username);
            return false;
        }
    }
}
