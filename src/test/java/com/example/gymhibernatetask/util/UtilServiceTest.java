package com.example.gymhibernatetask.util;

import com.example.gymhibernatetask.dto.UpdateRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilServiceTest {

    @InjectMocks
    private UtilService utilService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testGenerateUsername() {
        List<User> mockUsers = List.of(mock(User.class), mock(User.class));
        when(mockUsers.get(0).getUsername()).thenReturn("John.Doe");
        when(mockUsers.get(1).getUsername()).thenReturn("John.Doe.1");

        String username = utilService.generateUsername("John", "Doe", mockUsers);
        assertEquals("John.Doe.2", username);
    }

    @Test
    void testGenerateRandomPassword() {
        String password = utilService.generateRandomPassword(10);
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testUsernameExists() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("TestUser");
        List<User> users = Collections.singletonList(mockUser);

        boolean exist = utilService.usernameExists(users, "TestUser");
        assertTrue(exist);
    }

    @Test
    void testValidateUpdateRequest_success() {
        UpdateRequestDto mockDto = mock(UpdateRequestDto.class);
        when(mockDto.getUsername()).thenReturn("username");

        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> utilService.validateUpdateRequest(mockDto));
    }

    @Test
    void testValidateUpdateRequest_throwException() {
        UpdateRequestDto mockDto = mock(UpdateRequestDto.class);
        when(mockDto.getUsername()).thenReturn("username");

        List<User> mockUsers = List.of(mock(User.class));
        when(mockUsers.get(0).getUsername()).thenReturn("username");

        when(userRepository.findAll()).thenReturn(mockUsers);

        assertThrows(InvalidInputException.class, () -> utilService.validateUpdateRequest(mockDto));
    }
}