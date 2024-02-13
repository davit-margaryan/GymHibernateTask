package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.models.TrainingType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingDto {

    @NotBlank
    private String trainingName;

    @NotNull
    private Date date;

    private TrainingType trainingType;

    @NotNull
    private Number duration;

    @NotBlank
    private String trainerName;

    public TrainingDto(Training training) {
        this.trainingName = training.getName();
        this.date = training.getDate();
        this.trainingType = training.getTrainingType();
        this.duration = training.getDuration();
        if (training.getTrainer() != null) {
            this.trainerName = training.getTrainer().getUser().getFirstName();
        }
    }
}
