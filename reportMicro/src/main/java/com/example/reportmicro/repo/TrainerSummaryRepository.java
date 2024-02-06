package com.example.reportmicro.repo;

import com.example.reportmicro.model.TrainerSummary;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface TrainerSummaryRepository extends MongoRepository<TrainerSummary, Long> {
    Optional<TrainerSummary> findByUsername(String username);

}