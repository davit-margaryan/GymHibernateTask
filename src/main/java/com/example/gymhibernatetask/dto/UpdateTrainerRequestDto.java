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
public class UpdateTrainerRequestDto extends UpdateRequestDto {

    public UpdateTrainerRequestDto(@NotBlank String username, @NotBlank String firstName, @NotBlank String lastName, TrainingType specialization, boolean isActive) {
        super(username, firstName, lastName);
        this.specialization = specialization;
        this.isActive = isActive;
    }

    @NotNull
    private TrainingType specialization;

    @NotNull
    private boolean isActive;
}
