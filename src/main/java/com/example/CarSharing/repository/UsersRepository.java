package com.example.CarSharing.repository;

import com.example.CarSharing.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByToken(String token);

    Optional<Users> findByToken(String token);
}
