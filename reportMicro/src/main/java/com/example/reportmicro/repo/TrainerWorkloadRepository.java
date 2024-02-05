package com.example.reportmicro.repo;

import com.example.reportmicro.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {

    boolean existsByUsername(String username);

    List<TrainerWorkload> getAllByUsername(String username);

    void deleteAllByUsername(String username);
}
