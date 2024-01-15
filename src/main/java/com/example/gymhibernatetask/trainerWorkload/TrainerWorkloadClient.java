package com.example.gymhibernatetask.trainerWorkload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "report-microservice", url = "${report-microservice.url}", fallback = TrainerWorkloadFallback.class, configuration = FeignClientConfiguration.class)
public interface TrainerWorkloadClient {

    @PostMapping("/api/trainer-workload")
    ResponseEntity<?> manageTrainerWorkload(@RequestBody TrainerWorkload request, @RequestHeader(value = "X-Correlation-ID") String correlationId);

    @GetMapping("/api/trainer-workload/summary/{username}")
    ResponseEntity<TrainerSummary> getTrainerSummary(@PathVariable String username, @RequestHeader(value = "X-Correlation-ID") String correlationId);
}
