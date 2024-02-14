package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainee = :trainee " +
            "AND (:periodFrom IS NULL OR t.date >= :periodFrom) " +
            "AND (:periodTo IS NULL OR t.date <= :periodTo) " +
            "AND (:trainerName IS NULL OR t.trainer.user.firstName = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    List<Training> findByTraineeAndCriteria(
            @Param("trainee") Trainee trainee,
            @Param("periodFrom") Date periodFrom,
            @Param("periodTo") Date periodTo,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainer = :trainer " +
            "AND (:periodFrom IS NULL OR t.date >= :periodFrom) " +
            "AND (:periodTo IS NULL OR t.date <= :periodTo) " +
            "AND (:traineeName IS NULL OR t.trainee.user.firstName = :traineeName) " +
            "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    List<Training> findByTrainerAndCriteria(
            @Param("trainer") Trainer trainer,
            @Param("periodFrom") Date periodFrom,
            @Param("periodTo") Date periodTo,
            @Param("traineeName") String traineeName,
            @Param("trainingType") String trainingType
    );

    Optional<Training> findFirstByTraineeUserUsernameAndTrainerUserUsernameOrderByIdDesc(String traineeUsername, String trainerUsername);
}
