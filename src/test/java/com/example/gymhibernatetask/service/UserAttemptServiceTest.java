package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.auth.UserAttemptService;
import com.example.gymhibernatetask.models.UserLoginAttempt;
import com.example.gymhibernatetask.repository.UserLoginAttemptRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class UserAttemptServiceTest {

    @Mock
    private UserLoginAttemptRepository attemptRepository;

    @InjectMocks
    private UserAttemptService userAttemptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserLoginAttempt attempt = mock(UserLoginAttempt.class);
        when(attempt.getUsername()).thenReturn("testUser");
        when(attempt.getAttempts()).thenReturn(0);
        when(attempt.getLastAttempt()).thenReturn(LocalDateTime.now());

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
        UserLoginAttempt attempt = mock(UserLoginAttempt.class);
        when(attempt.getUsername()).thenReturn("testUser");
        when(attempt.getAttempts()).thenReturn(3);
        when(attempt.getLastAttempt()).thenReturn(LocalDateTime.now().minusMinutes(1));

        when(attemptRepository.getByUsername("testUser")).thenReturn(Optional.of(attempt));

        boolean isBlocked = userAttemptService.isBlocked("testUser");

        Assertions.assertTrue(isBlocked);
    }
}
