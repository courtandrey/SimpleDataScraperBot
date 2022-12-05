package com.github.courtandrey.simpledatascraperbot.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional
public interface DataRepository<T> extends JpaRepository<T, Long> {
}
