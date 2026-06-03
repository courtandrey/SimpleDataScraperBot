package com.github.courtandrey.simpledatascraperbot.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.OffsetDateTime;

@Service
public class AuditLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository repository;

    private final Sinks.Many<AuditLog> sink = Sinks.many().multicast().onBackpressureBuffer();

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Async
    @Transactional
    public void record(Long userId, CommandName commandName) {
        try {
            AuditLog saved = repository.save(new AuditLog(userId, commandName, OffsetDateTime.now()));
            sink.tryEmitNext(saved);
        } catch (Exception e) {
            LOGGER.warn("Failed to persist audit log for user {} / {}", userId, commandName, e);
        }
    }

    /** Backs the GraphQL subscription. */
    public Flux<AuditLog> stream() {
        return sink.asFlux();
    }
}
