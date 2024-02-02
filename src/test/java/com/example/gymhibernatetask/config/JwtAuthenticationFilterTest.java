package com.example.gymhibernatetask.config;


import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter authenticationFilter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        authenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    public void shouldPassFilterIfNoBearerToken() throws IOException, ServletException {
        when(request.getHeader("Authorization")).thenReturn("");

        authenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    public void shouldAuthenticateIfBearerTokenIsValid() throws IOException, ServletException {
        String userEmail = "test@test.com";
        String jwt = "Bearer jwtTest";
        Token token = new Token();
        token.setExpired(false);
        token.setRevoked(false);
        token.setToken(jwt);

        UserDetails userDetails = new User(userEmail, "password", new ArrayList<>());

        when(request.getHeader("Authorization")).thenReturn(jwt);
        when(jwtService.extractUsername(anyString())).thenReturn(userEmail); // changed line
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true); // changed line
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(token)); // changed line

        authenticationFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, times(1)).loadUserByUsername(userEmail);
        verify(jwtService, times(1)).isTokenValid(anyString(), eq(userDetails)); // changed line
        verify(filterChain, times(1)).doFilter(request, response);
    }

}