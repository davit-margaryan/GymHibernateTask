package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponseDto {

    private String firstName;

    private String lastName;

    private TrainingType specialization;

    private boolean isActive;

    private List<Trainee> trainees;
}
