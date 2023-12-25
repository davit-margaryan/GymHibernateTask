package com.example.gymhibernatetask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTraineeRequestDto extends UpdateRequestDto {

    @NotNull
    private Date dateOfBirth;

    @NotBlank
    private String address;

    @NotNull
    private boolean isActive;
}
