package com.example.gymhibernatetask.auth;


import com.example.gymhibernatetask.dto.ChangePasswordRequest;
import com.example.gymhibernatetask.dto.CreateTraineeRequestDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register/trainee")
    public ResponseEntity<AuthenticationResponse> registerTrainee(@RequestBody CreateTraineeRequestDto request) {
        return ResponseEntity.ok(service.registerTrainee(request));
    }

    @PostMapping("/register/trainer")
    public ResponseEntity<AuthenticationResponse> registerTrainer(@RequestBody CreateTrainerRequestDto request) {
        return ResponseEntity.ok(service.registerTrainer(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws AccountLockedException {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(request);
        return ResponseEntity.ok().build();
    }
}