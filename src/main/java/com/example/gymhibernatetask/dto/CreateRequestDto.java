package com.example.gymhibernatetask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
