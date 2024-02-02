package com.example.gymhibernatetask.dto;

import com.example.gymhibernatetask.models.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CreateTrainerRequestDto extends CreateRequestDto {

    public CreateTrainerRequestDto(@NotBlank String firstName, @NotBlank String lastName, TrainingType specialization) {
        super(firstName, lastName);
        this.specialization = specialization;
    }

    @NotNull
    private TrainingType specialization;
}
