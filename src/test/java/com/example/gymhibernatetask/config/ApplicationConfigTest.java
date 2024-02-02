package com.example.gymhibernatetask.config;

import com.example.gymhibernatetask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    private ApplicationConfig config;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        config = new ApplicationConfig(userRepository);
    }

    @Test
    public void shouldCreateUserDetailsService() {
        UserDetailsService service = config.userDetailsService();
        assertNotNull(service);
    }

    @Test
    public void shouldCreateAuthenticationProvider() {
        AuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    public void shouldCreatePasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    public void shouldCreateCorsConfigurationSource() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        assertNotNull(source);
    }
}