package com.example.gymhibernatetask.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "trainers", cascade = CascadeType.ALL)
    private List<Trainee> trainees;


    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToOne
    @JoinColumn(name = "specialization", nullable = false)
    private TrainingType specialization;

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                "user=" + user +
                "specialization=" + specialization +
                "}";
    }
}
