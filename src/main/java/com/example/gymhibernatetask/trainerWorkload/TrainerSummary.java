package com.example.gymhibernatetask.trainerWorkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerSummary {

    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private List<Integer> years;
    private Map<String, Map<String, Integer>> monthlySummary;

}