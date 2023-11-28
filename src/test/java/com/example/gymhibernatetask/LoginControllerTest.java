package com.example.gymhibernatetask;

import com.example.gymhibernatetask.controller.LoginController;
import com.example.gymhibernatetask.service.LoginService;
import com.example.gymhibernatetask.util.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginControllerTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLogin() {
        String username = "testUser";
        String password = "testPassword";

        when(loginService.login(username, password)).thenReturn(true);

        ResponseEntity<String> response = loginController.login(username, password);

        verify(loginService).login(username, password);
        verify(transactionLogger).logTransactionRequest("Received login request");

        assertEquals(ResponseEntity.ok("Login successful"), response);
    }

    @Test
    void testChangeLogin() {
        String username = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(loginService.changeLogin(username, oldPassword, newPassword)).thenReturn(true);

        ResponseEntity<String> response = loginController.changeLogin(username, oldPassword, newPassword);

        verify(loginService).changeLogin(username, oldPassword, newPassword);
        verify(transactionLogger).logTransactionRequest("Received change login request");

        assertEquals(ResponseEntity.ok("Password changed successfully"), response);
    }
}
