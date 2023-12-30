package com.example.gymhibernatetask.auth;

import com.example.gymhibernatetask.config.JwtService;
import com.example.gymhibernatetask.dto.ChangePasswordRequest;
import com.example.gymhibernatetask.dto.CreateTraineeRequestDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final AuthenticationManager authenticationManager;
    private final MeterRegistry meterRegistry;
    private final PasswordEncoder passwordEncoder;
    private final UserAttemptService userAttemptService;



    private Counter counter;

    @PostConstruct
    public void init() {
        counter = meterRegistry.counter("successful.login.counter");
    }

    @Transactional
    public AuthenticationResponse registerTrainee(CreateTraineeRequestDto request) {
        logger.info("Creating trainee with request: {}", request);

        Trainee trainee = new Trainee();
        User user = userService.createUser(request);
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        traineeRepository.save(trainee);

        logger.info("Trainee created successfully. Username: {}", user.getUsername());

        return authenticateUserAfterRegister(user);
    }


    @Transactional
    public AuthenticationResponse registerTrainer(CreateTrainerRequestDto request) {
        logger.info("Creating trainer with request: {}", request);

        Trainer trainer = new Trainer();
        User user = userService.createUser(request);
        trainer.setUser(user);
        if (request.getSpecialization() != null) {
            trainer.setSpecialization(request.getSpecialization());
        }

        trainerRepository.save(trainer);
        logger.info("Trainer created successfully. Username: {}", user.getUsername());

        return authenticateUserAfterRegister(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AccountLockedException {
        String username = request.getUsername();

        if (userAttemptService.isBlocked(username)) {
            logger.warn("User account is blocked due to too many failed login attempts: {}", username);

            throw new AccountLockedException(username);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            userAttemptService.loginSucceeded(username);

            var user = repository.getByUsername(request.getUsername()).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            counter.increment();

            logger.info("User logged in successfully: {}", username);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException ex) {
            userAttemptService.loginFailed(username);
            logger.warn("Invalid login attempt: {}", username);
            throw ex;
        }
    }
    @Transactional
    public void changePassword(ChangePasswordRequest dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            throw new IllegalStateException("Principal not found in security context");
        }
        String username = userDetails.getUsername();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        dto.getOldPassword()
                )
        );

        User user = repository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        repository.save(user);

        revokeAllUserTokens(user);

        String jwtToken = jwtService.generateToken(user);

        saveUserToken(user, jwtToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.repository.getByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private AuthenticationResponse authenticateUserAfterRegister(User user) {
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}