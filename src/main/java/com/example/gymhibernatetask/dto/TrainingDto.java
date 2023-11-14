package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDto {

    private String trainingName;

    private Date date;

    private TrainingType trainingType;

    private Number duration;

    private String trainerName;

}
