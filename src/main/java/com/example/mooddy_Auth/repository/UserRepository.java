package com.example.mooddy_Auth.repository;

import com.example.mooddy_Auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String username);
    boolean existsByEmail(String email);

    String email(String email);
}
