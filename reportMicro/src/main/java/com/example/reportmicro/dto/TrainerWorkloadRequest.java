package com.example.reportmicro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Date trainingDate;
    private Number trainingDuration;
    private String actionType;
}