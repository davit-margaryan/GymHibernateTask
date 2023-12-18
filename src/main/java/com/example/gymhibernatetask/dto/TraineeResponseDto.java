package com.example.gymhibernatetask.dto;

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
}
