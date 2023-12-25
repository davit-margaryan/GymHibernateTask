package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeResponseDto {

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;

    private List<TrainerListResponseDto> trainers;

    public TraineeResponseDto(Trainee trainee) {
        if (trainee.getUser() != null) {
            this.firstName = trainee.getUser().getFirstName();
            this.lastName = trainee.getUser().getLastName();
            this.isActive = trainee.getUser().isActive();
        }
        this.dateOfBirth = trainee.getDateOfBirth();
        this.address = trainee.getAddress();
        List<Trainer> trainersList = trainee.getTrainers();
        this.trainers = trainersList.stream()
                .map(TrainerListResponseDto::new)
                .toList();
    }
}
