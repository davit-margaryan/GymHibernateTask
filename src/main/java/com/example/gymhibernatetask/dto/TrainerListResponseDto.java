package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Trainer;
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

    public TrainerListResponseDto(Trainer trainer) {
        this.setUsername(trainer.getUser().getUsername());
        this.setFirstName(trainer.getUser().getFirstName());
        this.setLastName(trainer.getUser().getLastName());
        this.setSpecialization(trainer.getSpecialization());
    }
}
