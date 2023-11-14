package com.example.gymhibernatetask.repository;

import com.example.gymhibernatetask.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByUsername(String username);

    boolean existsByUsername(String username);
}
