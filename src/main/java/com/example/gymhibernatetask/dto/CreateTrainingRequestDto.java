package com.example.gymhibernatetask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrainingRequestDto {

    private String traineeUsername;

    private String trainerUsername;

    private String trainingName;

    private Date date;

    private Number duration;

}
