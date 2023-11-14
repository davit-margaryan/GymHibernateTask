package com.example.gymhibernatetask.dto;

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

    private Date dateOfBirth;

    private String address;

    private boolean isActive;

}
