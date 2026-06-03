package com.github.courtandrey.simpledatascraperbot.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    @Query("""
           select a.userId as userId, count(a) as total, max(a.timestamp) as lastSeen
           from AuditLog a
           where a.userId in :ids
           group by a.userId
           """)
    List<UserStatsProjection> findStatsByUserIds(@Param("ids") Collection<Long> ids);
}
