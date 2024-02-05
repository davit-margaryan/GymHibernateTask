package com.example.reportmicro.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "trainersSummary")
@CompoundIndex(def = "{'firstName': 1, 'lastName': 1}")
public class TrainerSummary {

    @Id
    private String trainerSummaryId;

    @NotEmpty(message = "Username is mandatory")
    private String username;

    @NotEmpty(message = "First name is mandatory")
    @Field("first_name")
    private String firstName;

    @NotEmpty(message = "Last name is mandatory")
    @Field("last_name")
    private String lastName;

    private boolean status;

    private List<Integer> years;

    private Map<String, Map<String, Integer>> monthlySummary;

}