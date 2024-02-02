package com.example.gymhibernatetask.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setup() throws UnsupportedEncodingException {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        String secretKey = Base64.getEncoder().encodeToString("HvN5RvaUGR1HgRJDWr6xojfn3veBoKTq".getBytes("UTF-8"));
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 60000L);
    }

    @Test
    public void shouldGenerateToken() {
        String username = "user";
        when(userDetails.getUsername()).thenReturn(username);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    public void shouldGenerateTokenWithExtraClaims() {
        String username = "user";
        Map<String, Object> claims = new HashMap<>();
        claims.put("extra", "claim");
        when(userDetails.getUsername()).thenReturn(username);

        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    public void shouldGenerateRefreshToken() {
        String username = "user";
        when(userDetails.getUsername()).thenReturn(username);

        String token = jwtService.generateRefreshToken(userDetails);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    public void shouldValidateToken() {
        String username = "user";
        when(userDetails.getUsername()).thenReturn(username);
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void shouldFailToValidateTokenWhenTokenIsInvalid() {
        UserDetails user = User.withUsername("user").password("pass").roles("USER").build();
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}