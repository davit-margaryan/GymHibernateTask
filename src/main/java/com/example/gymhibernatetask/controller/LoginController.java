package com.example.gymhibernatetask.controller;

import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.util.TransactionLogger;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Api(tags = "Authentication API")
@RequestMapping("/auth")
public class LoginController {

    private final TransactionLogger transactionLogger;

    private final LoginService loginService;

    public LoginController(TransactionLogger transactionLogger, LoginService loginService) {
        this.transactionLogger = transactionLogger;
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        UUID transactionId = transactionLogger.logTransactionRequest("Received login request");

        loginService.login(username, password);
        transactionLogger.logTransactionSuccess("Login successful", transactionId, username);

        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/change-login")
    public ResponseEntity<String> changeLogin(@RequestParam String username,
                                              @RequestParam String oldPassword,
                                              @RequestParam String newPassword) {
        UUID transactionId = transactionLogger.logTransactionRequest("Received change login request");

        loginService.changeLogin(username, oldPassword, newPassword);
        transactionLogger.logTransactionSuccess("Password changed successfully", transactionId, username);

        return ResponseEntity.ok("Password changed successfully");
    }
}
