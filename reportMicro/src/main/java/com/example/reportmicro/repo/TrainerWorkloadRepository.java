package com.example.reportmicro.repo;

import com.example.reportmicro.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload,Long> {

    boolean existsByUsername(String username);

    void deleteByUsername(String username);
}
