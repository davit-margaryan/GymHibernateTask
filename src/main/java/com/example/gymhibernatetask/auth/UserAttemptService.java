package com.example.gymhibernatetask.auth;

import com.example.gymhibernatetask.models.UserLoginAttempt;
import com.example.gymhibernatetask.repository.UserLoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_TIME_MINUTES = 5;

    private final UserLoginAttemptRepository attemptRepository;
    private final Logger logger = LoggerFactory.getLogger(UserAttemptService.class);

    public void loginSucceeded(String username) {
        Optional<UserLoginAttempt> userLoginAttemptOptional = attemptRepository.getByUsername(username);
        userLoginAttemptOptional.ifPresent(attemptRepository::delete);

        logger.info("User {} logged in successfully, login attempts deleted.", username);
    }

    public void loginFailed(String username) {
        UserLoginAttempt userLoginAttempt = attemptRepository.getByUsername(username)
                .orElseGet(() -> {
                    UserLoginAttempt newUserLoginAttempt = new UserLoginAttempt();
                    newUserLoginAttempt.setUsername(username);
                    newUserLoginAttempt.setAttempts(0);
                    newUserLoginAttempt.setLastAttempt(LocalDateTime.now());
                    attemptRepository.save(newUserLoginAttempt);
                    return newUserLoginAttempt;
                });

        userLoginAttempt.setAttempts(userLoginAttempt.getAttempts() + 1);
        userLoginAttempt.setLastAttempt(LocalDateTime.now());
        attemptRepository.save(userLoginAttempt);

        logger.warn("User {} failed to login, login attempts increased to {}.", username, userLoginAttempt.getAttempts());
    }

    public boolean isBlocked(String username) {
        Optional<UserLoginAttempt> userLoginAttemptOptional = attemptRepository.getByUsername(username);

        if (userLoginAttemptOptional.isPresent()) {
            UserLoginAttempt userLoginAttempt = userLoginAttemptOptional.get();
            if (userLoginAttempt.getAttempts() >= MAX_ATTEMPTS) {
                LocalDateTime unblockTime = userLoginAttempt.getLastAttempt().plusMinutes(BLOCK_TIME_MINUTES);
                if (LocalDateTime.now().isBefore(unblockTime)) {
                    long minutesLeft = ChronoUnit.MINUTES
                            .between(LocalDateTime.now(), unblockTime);
                    long secondsLeft = ChronoUnit.SECONDS
                            .between(LocalDateTime.now().plusMinutes(minutesLeft), unblockTime);

                    logger.warn("User {} has been temporarily blocked due to {} unsuccessful login attempts." +
                                    " It will be unblocked in {} minutes and {} seconds.",
                            username, userLoginAttempt.getAttempts(), minutesLeft, secondsLeft);

                    return true;
                }
            }
        }
        return false;
    }
}