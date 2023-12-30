package com.example.gymhibernatetask;

import com.example.gymhibernatetask.auth.UserAttemptService;
import com.example.gymhibernatetask.models.UserLoginAttempt;
import com.example.gymhibernatetask.repository.UserLoginAttemptRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

class UserAttemptServiceTest {

    @Mock
    private UserLoginAttemptRepository attemptRepository;

    @InjectMocks
    private UserAttemptService userAttemptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserLoginAttempt attempt = new UserLoginAttempt();
        attempt.setUsername("testUser");
        attempt.setAttempts(0);
        attempt.setLastAttempt(LocalDateTime.now());

        when(attemptRepository.getByUsername("testUser")).thenReturn(Optional.of(attempt));
        when(attemptRepository.getByUsername("nonexistentUser")).thenReturn(Optional.empty());
    }

    @Test
    void whenLoginSucceeded_thenAttemptsReset() {
        userAttemptService.loginSucceeded("testUser");

        verify(attemptRepository, times(1)).delete(any(UserLoginAttempt.class));
    }

    @Test
    void whenLoginFailed_thenAttemptsIncrease() {
        userAttemptService.loginFailed("testUser");

        verify(attemptRepository, times(1)).save(any(UserLoginAttempt.class));
    }

    @Test
    void whenUserIsBlocked_thenReturnsTrue() {
        UserLoginAttempt attempt = new UserLoginAttempt();
        attempt.setUsername("testUser");
        attempt.setAttempts(3);
        attempt.setLastAttempt(LocalDateTime.now().minusMinutes(1));

        when(attemptRepository.getByUsername("testUser")).thenReturn(Optional.of(attempt));

        boolean isBlocked = userAttemptService.isBlocked("testUser");

        Assertions.assertTrue(isBlocked);
    }
}
