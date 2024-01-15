package com.example.gymhibernatetask;

import com.example.gymhibernatetask.auth.AuthenticationController;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.dto.ChangePasswordRequest;
import com.example.gymhibernatetask.dto.CreateTraineeRequestDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import com.example.gymhibernatetask.models.TrainingType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationService service;

    @InjectMocks
    private AuthenticationController controller;

    private CreateTraineeRequestDto traineeRequestDto;
    private CreateTrainerRequestDto trainerRequestDto;
    private AuthenticationRequest authenticationRequest;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeRequestDto = new CreateTraineeRequestDto();
        traineeRequestDto.setFirstName("John");
        traineeRequestDto.setLastName("Doe");
        traineeRequestDto.setAddress("123 ABC Street");
        traineeRequestDto.setDateOfBirth(new Date());

        trainerRequestDto = new CreateTrainerRequestDto();
        trainerRequestDto.setFirstName("Jane");
        trainerRequestDto.setLastName("Doe");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Football");
        trainerRequestDto.setSpecialization(trainingType);

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("JohnDoe");
        authenticationRequest.setPassword("123ABC");

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("123ABC");
        changePasswordRequest.setNewPassword("ABC123");
    }

    @Test
    void testRegisterTrainee() {
        AuthenticationResponse response = new AuthenticationResponse();
        when(service.registerTrainee(traineeRequestDto)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.registerTrainee(traineeRequestDto);

        verify(service).registerTrainee(traineeRequestDto);
        assertEquals(response, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRegisterTrainer() {
        AuthenticationResponse response = new AuthenticationResponse();
        when(service.registerTrainer(trainerRequestDto)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.registerTrainer(trainerRequestDto);

        verify(service).registerTrainer(trainerRequestDto);
        assertEquals(response, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testAuthenticate() throws AccountLockedException {
        AuthenticationResponse response = new AuthenticationResponse();
        when(service.authenticate(authenticationRequest)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.authenticate(authenticationRequest);

        verify(service).authenticate(authenticationRequest);
        assertEquals(response, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRefreshToken() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        controller.refreshToken(request, response);

        verify(service).refreshToken(request, response);
    }

    @Test
    void testChangePassword() {
        ResponseEntity<String> result = controller.changePassword(changePasswordRequest);

        verify(service).changePassword(changePasswordRequest);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}