package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long id);
    Optional<User> findByUsername(String username);
}
