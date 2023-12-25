package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Trainee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeListResponseDto {

    private String username;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;

    public TraineeListResponseDto (Trainee trainee) {
        this.username = trainee.getUser().getUsername();
        this.firstName = trainee.getUser().getFirstName();
        this.lastName = trainee.getUser().getLastName();
        this.address = trainee.getAddress();
        this.dateOfBirth = trainee.getDateOfBirth();
    }
}
