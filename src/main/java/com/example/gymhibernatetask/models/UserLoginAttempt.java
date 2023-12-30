package com.example.gymhibernatetask.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_attempts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;


    @Column(name = "attempts")
    private int attempts;

    @Column(name = "attempt_date")
    private LocalDateTime lastAttempt;

}