package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    void deleteTraineeByUserUsername(String username);

    Optional<Trainee> getTraineeByUserUsername(String username);

    void deleteTrainers();
}
