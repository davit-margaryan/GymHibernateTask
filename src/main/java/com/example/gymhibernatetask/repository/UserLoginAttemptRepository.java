package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.models.UserLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginAttemptRepository extends JpaRepository<UserLoginAttempt,Long> {
    Optional<UserLoginAttempt> getByUsername(String username);

    void deleteByUsername(String username);
}
