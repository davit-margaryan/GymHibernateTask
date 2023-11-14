package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t WHERE t.user.isActive = true")
    List<Trainer> findAllActiveTrainers();

    Optional<Trainer> getTrainerByUserUsername(String username);

}
