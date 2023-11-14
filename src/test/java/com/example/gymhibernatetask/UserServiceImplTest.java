package com.example.gymhibernatetask;

import com.example.gymhibernatetask.dto.CreateRequestDto;
import com.example.gymhibernatetask.exception.InvalidInputException;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.impl.UserServiceImpl;
import com.example.gymhibernatetask.util.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        CreateRequestDto requestDto = mock(CreateRequestDto.class);
        User mockedUser = mock(User.class);

        when(requestDto.getFirstName()).thenReturn("John");
        when(requestDto.getLastName()).thenReturn("Doe");
        when(utilService.generateRandomPassword(10)).thenReturn("randomPassword");
        when(utilService.generateUsername(anyString(), anyString(), anyList())).thenReturn("john.doe");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User createdUser = userService.createUser(requestDto);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("john.doe", createdUser.getUsername());
        assertEquals("randomPassword", createdUser.getPassword());
        assertTrue(createdUser.isActive());
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        assertNotNull(createdUser.getId());
    }


    @Test
    void createUser_failure_missingFirstName() {
        CreateRequestDto requestDto = mock(CreateRequestDto.class);

        when(requestDto.getLastName()).thenReturn("Doe");

        assertThrows(InvalidInputException.class, () -> userService.createUser(requestDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_failure_missingLastName() {
        CreateRequestDto requestDto = mock(CreateRequestDto.class);

        when(requestDto.getFirstName()).thenReturn("John");

        assertThrows(InvalidInputException.class, () -> userService.createUser(requestDto));

        verify(userRepository, never()).save(any(User.class));
    }
}
