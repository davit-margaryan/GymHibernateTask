package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateRequestDto;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.impl.UserServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UtilService utilService;

    @Mock
    private CreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(createRequestDto.getFirstName()).thenReturn("John");
        when(createRequestDto.getLastName()).thenReturn("Doe");
    }

    @Test
    void testCreateUser() {
        User mockUser = mock(User.class);
        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");
        when(mockUser.isActive()).thenReturn(true);

        when(utilService.generateUsername(anyString(), anyString(), any())).thenReturn("johndoe");
        when(utilService.generateRandomPassword(anyInt())).thenReturn("randompassword");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        User result = userServiceImpl.createUser(createRequestDto);

        verify(utilService).generateUsername(anyString(), anyString(), any());
        verify(utilService).generateRandomPassword(anyInt());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userRepository).findAll();

        assertNotNull(result);
    }
}