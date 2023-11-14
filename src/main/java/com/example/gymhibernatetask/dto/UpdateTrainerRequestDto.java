package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrainerRequestDto extends UpdateRequestDto {

    private TrainingType specialization;

    private boolean isActive;
}
