package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.auth.AuthenticationController;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.dto.ChangePasswordRequest;
import com.example.gymhibernatetask.dto.CreateTraineeRequestDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService service;

    @InjectMocks
    private AuthenticationController controller;

    @Test
    void testRegisterTrainee() {
        CreateTraineeRequestDto traineeRequestDto = mock(CreateTraineeRequestDto.class);
        when(service.registerTrainee(any(CreateTraineeRequestDto.class))).thenReturn(mock(AuthenticationResponse.class));

        ResponseEntity<AuthenticationResponse> result = controller.registerTrainee(traineeRequestDto);

        verify(service).registerTrainee(any(CreateTraineeRequestDto.class));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRegisterTrainer() {
        CreateTrainerRequestDto trainerRequestDto = mock(CreateTrainerRequestDto.class);
        when(service.registerTrainer(any(CreateTrainerRequestDto.class))).thenReturn(mock(AuthenticationResponse.class));

        ResponseEntity<AuthenticationResponse> result = controller.registerTrainer(trainerRequestDto);

        verify(service).registerTrainer(any(CreateTrainerRequestDto.class));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testAuthenticate() throws AccountLockedException {
        AuthenticationRequest authenticationRequest = mock(AuthenticationRequest.class);
        when(service.authenticate(any(AuthenticationRequest.class))).thenReturn(mock(AuthenticationResponse.class));

        ResponseEntity<AuthenticationResponse> result = controller.authenticate(authenticationRequest);

        verify(service).authenticate(any(AuthenticationRequest.class));
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
        ChangePasswordRequest changePasswordRequest = mock(ChangePasswordRequest.class);
        ResponseEntity<String> result = controller.changePassword(changePasswordRequest);

        verify(service).changePassword(changePasswordRequest);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
