package com.github.courtandrey.simpledatascraperbot.audit;

import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;

public final class GraphQlTypes {

    private GraphQlTypes() { }

    public record AuditLogFilter(
            Long userId,
            CommandName commandName,
            OffsetDateTime from,
            OffsetDateTime to
    ) { }

    public record AuditLogPage(
            List<AuditLog> content,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
        public static AuditLogPage of(Page<AuditLog> p) {
            return new AuditLogPage(
                    p.getContent(),
                    p.getTotalElements(),
                    p.getTotalPages(),
                    p.getNumber(),
                    p.getSize()
            );
        }
    }

    public record AuditUser(Long id, long totalCommands, OffsetDateTime lastSeen) { }
}
