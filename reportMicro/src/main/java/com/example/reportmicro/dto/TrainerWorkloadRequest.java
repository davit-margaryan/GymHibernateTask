package com.example.reportmicro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String traineeUsername;

    private boolean isActive;

    @NotNull
    private Date trainingDate;

    @NotNull
    private Number trainingDuration;

    @NotBlank
    private String actionType;

}