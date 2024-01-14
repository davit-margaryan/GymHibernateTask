package com.example.gymhibernatetask.trainerWorkload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "report-microservice", url = "${report-microservice.url}",fallback = TrainerWorkloadClientFallback.class)
public interface TrainerWorkloadClient {

    @PostMapping("/api/trainerWorkload")
    ResponseEntity<?> manageTrainerWorkload(@RequestBody TrainerWorkload request, @RequestHeader(value = "X-Correlation-ID") String correlationId);

}
