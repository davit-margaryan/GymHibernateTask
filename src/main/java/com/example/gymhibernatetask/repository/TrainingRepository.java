package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainee = :trainee " +
            "AND (:periodFrom IS NULL OR t.date >= :periodFrom) " +
            "AND (:periodTo IS NULL OR t.date <= :periodTo) " +
            "AND (:trainerName IS NULL OR t.trainer.user.firstName = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType = :trainingType)")
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
            "AND (:trainingType IS NULL OR t.trainingType = :trainingType)")
    List<Training> findByTrainerAndCriteria(
            @Param("trainer") Trainer trainer,
            @Param("periodFrom") Date periodFrom,
            @Param("periodTo") Date periodTo,
            @Param("traineeName") String traineeName,
            @Param("trainingType") String trainingType
    );

    List<Training> findAllByTraineeId(Long id);

    void deleteByTraineeId(Long id);
}
