package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.repository.TrainingTypeRepository;
import com.example.gymhibernatetask.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceTest {

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    public void getAllTest() {
        TrainingType type1 = mock(TrainingType.class);
        TrainingType type2 = mock(TrainingType.class);
        List<TrainingType> fakeList = Arrays.asList(type1, type2);

        when(trainingTypeRepository.findAll()).thenReturn(fakeList);

        List<TrainingType> result = trainingTypeService.getAll();
        assertEquals(fakeList, result);
    }
}