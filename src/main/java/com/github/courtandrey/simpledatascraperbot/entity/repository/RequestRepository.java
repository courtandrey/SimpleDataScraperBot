package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional
public interface RequestRepository<T extends Request> extends JpaRepository<T, Long> {
}
