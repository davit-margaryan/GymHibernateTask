package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerListResponseDto {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingType specialization;

}
