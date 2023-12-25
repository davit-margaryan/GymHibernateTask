package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Trainer;
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

    private List<TraineeListResponseDto> trainees;

    public TrainerResponseDto (Trainer trainer) {
        if (trainer.getUser() != null) {
            this.firstName = trainer.getUser().getFirstName();
            this.lastName = trainer.getUser().getLastName();
            this.isActive = trainer.getUser().isActive();
        }
        this.specialization = trainer.getSpecialization();
        this.trainees = trainer.getTrainees().stream()
                .map(TraineeListResponseDto::new)
                .toList();
    }
}
