package com.example.gymhibernatetask;

import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.auth.UserAttemptService;
import com.example.gymhibernatetask.config.JwtService;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.token.TokenRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.security.auth.login.AccountLockedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserAttemptService userAttemptService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");

        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(userRepository.getByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("testToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("testRefreshToken");
        when(userAttemptService.isBlocked(user.getUsername())).thenReturn(false);
        authenticationService.init();
    }

    @Test
    void testAuthenticate() throws AccountLockedException {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(user.getUsername());
        authenticationRequest.setPassword("testPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        authenticationService.authenticate(authenticationRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userAttemptService).loginSucceeded(anyString());
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void testAuthenticateWithFailedAttempt() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(user.getUsername());
        authenticationRequest.setPassword("invalidPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        try {
            authenticationService.authenticate(authenticationRequest);
        } catch (BadCredentialsException e) {
            // Exception is expected, do nothing.
        } catch (AccountLockedException e) {
            throw new RuntimeException(e);
        }

        verify(userAttemptService).loginFailed(anyString());
    }

    @Test
    void testRefreshToken() throws IOException {
        final String authHeader = "Bearer testToken";
        when(jwtService.extractUsername(authHeader.substring(7))).thenReturn(user.getUsername());
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        });
        authenticationService.refreshToken(request, response);

        verify(jwtService).extractUsername(anyString());
        verify(jwtService).isTokenValid(anyString(), any());
        verify(jwtService).generateToken(user);
    }
}