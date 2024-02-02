package com.example.gymhibernatetask.config;

import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    private LogoutService logoutService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        logoutService = new LogoutService(tokenRepository);
    }

    @Test
    void shouldLogoutUser() {
        String jwt = "Bearer jwtTest";
        Token token = new Token();
        token.setExpired(false);
        token.setRevoked(false);

        when(request.getHeader("Authorization")).thenReturn(jwt);
        when(tokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        logoutService.logout(request, null, null);

        assertEquals(true, token.isExpired());
        assertEquals(true, token.isRevoked());
        verify(tokenRepository, times(1)).save(any());
    }

    @Test
    void shouldNotLogoutUserWithoutBearerToken() {
        when(request.getHeader("Authorization")).thenReturn("");

        logoutService.logout(request, null, null);

        verify(tokenRepository, never()).findByToken(any());
    }
}