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
public class CreateTraineeRequestDto extends CreateRequestDto {

    public CreateTraineeRequestDto(@NotBlank String firstName, @NotBlank String lastName, Date dateOfBirth, String address) {
        super(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    @NotNull
    private Date dateOfBirth;

    @NotBlank
    private String address;
}
