package com.github.courtandrey.simpledatascraperbot.audit;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "audit_log",
    indexes = {
        @Index(name = "idx_audit_user", columnList = "user_id"),
        @Index(name = "idx_audit_ts", columnList = "timestamp")
    }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_name", nullable = false, length = 32)
    private CommandName commandName;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    protected AuditLog() { } // JPA

    public AuditLog(Long userId, CommandName commandName, OffsetDateTime timestamp) {
        this.userId = userId;
        this.commandName = commandName;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public CommandName getCommandName() { return commandName; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
