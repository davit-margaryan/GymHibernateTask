package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.repository.TrainingTypeRepository;
import com.example.gymhibernatetask.service.TrainingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public List<TrainingType> getAll() {
        return trainingTypeRepository.findAll();
    }
}
