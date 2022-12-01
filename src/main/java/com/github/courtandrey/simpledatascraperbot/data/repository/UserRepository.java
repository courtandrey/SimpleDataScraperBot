package com.github.courtandrey.simpledatascraperbot.data.repository;

import com.github.courtandrey.simpledatascraperbot.configuration.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(Long id);
}
