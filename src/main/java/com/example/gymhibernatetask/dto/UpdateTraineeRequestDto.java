package com.example.gymhibernatetask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdateTraineeRequestDto extends UpdateRequestDto {

    public UpdateTraineeRequestDto(@NotBlank String username, @NotBlank String firstName, @NotBlank String lastName, Date dateOfBirth, String address, boolean isActive) {
        super(username, firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }

    @NotNull
    private Date dateOfBirth;

    @NotBlank
    private String address;

    @NotNull
    private boolean isActive;
}
