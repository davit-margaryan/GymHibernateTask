package com.example.reportmicro.controller;

import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.service.TrainerWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainerWorkload")
public class TrainerWorkloadController {

    @Autowired
    private TrainerWorkloadService service;

    @PostMapping
    public ResponseEntity<?> manageTrainerWorkload(@RequestBody TrainerWorkloadRequest request, @RequestHeader(value = "X-Correlation-ID") String correlationId) {
        service.manageTrainerWorkload(request, correlationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}