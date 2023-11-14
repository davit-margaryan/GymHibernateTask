package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginServiceImpl implements LoginService {

    private final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String username, String password) {
        logger.info("Attempting login for username: {}", username);

        Optional<User> byUsername = userRepository.getByUsername(username);

        boolean isSuccess = byUsername.filter(user -> password.equals(user.getPassword())).isPresent();

        if (isSuccess) {
            logger.info("Login successful for username: {}", username);
        } else {
            logger.warn("Login failed for username: {}", username);
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
