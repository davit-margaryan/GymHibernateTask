package com.example.gymhibernatetask;

import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class LoginServiceImplTest {

    private String username = "john.doe";
    private String password = "password123";
    private String oldPassword = "oldPassword";
    private String newPassword = "newPassword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_successful() {
        username = "john.doe";
        password = "password123";

        when(userRepository.getByUsername(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(password);

        boolean result = loginService.login(username, password);

        assertTrue(result);
    }

    @Test
    void login_unsuccessful() {
        username = "john.doe";
        password = "password123";

        when(userRepository.getByUsername(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("wrongPassword");

        boolean result = loginService.login(username, password);

        assertFalse(result);
    }

    @Test
    void login_userNotFound() {
        username = "john.doe";
        password = "password123";

        when(userRepository.getByUsername(username)).thenReturn(Optional.empty());

        boolean result = loginService.login(username, password);

        assertFalse(result);
    }

    @Test
    void changeLogin_successful() {
        username = "john.doe";
        oldPassword = "oldPassword";
        newPassword = "newPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(oldPassword);

        when(userRepository.getByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = loginService.changeLogin(username, oldPassword, newPassword);

        assertTrue(result);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void changeLogin_unsuccessful_invalidOldPassword() {
        String username = "john.doe";
        oldPassword = "oldPassword";
        newPassword = "newPassword";

        when(userRepository.getByUsername(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("wrongOldPassword");

        boolean result = loginService.changeLogin(username, oldPassword, newPassword);

        assertFalse(result);
        assertNotEquals(newPassword, user.getPassword());
    }

    @Test
    void changeLogin_unsuccessful_emptyNewPassword() {
        username = "john.doe";
        oldPassword = "oldPassword";
        String newPassword = "";

        boolean result = loginService.changeLogin(username, oldPassword, newPassword);

        assertFalse(result);
    }
}
