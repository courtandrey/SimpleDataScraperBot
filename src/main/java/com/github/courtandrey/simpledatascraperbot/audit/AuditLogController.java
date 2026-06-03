package com.github.courtandrey.simpledatascraperbot.audit;

import com.github.courtandrey.simpledatascraperbot.audit.GraphQlTypes.AuditLogFilter;
import com.github.courtandrey.simpledatascraperbot.audit.GraphQlTypes.AuditLogPage;
import com.github.courtandrey.simpledatascraperbot.audit.GraphQlTypes.AuditUser;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class AuditLogController {

    private final AuditLogRepository repository;
    private final AuditLogService service;

    public AuditLogController(AuditLogRepository repository, AuditLogService service) {
        this.repository = repository;
        this.service = service;
    }

    @QueryMapping
    public AuditLog auditLog(@Argument Long id) {
        return repository.findById(id).orElse(null);
    }

    @QueryMapping
    public AuditLogPage auditLogs(@Argument AuditLogFilter filter,
                                  @Argument int page,
                                  @Argument int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return AuditLogPage.of(repository.findAll(toSpecification(filter), pageable));
    }

    @QueryMapping
    public AuditUser user(@Argument Long id) {
        return repository.findStatsByUserIds(List.of(id)).stream()
                .findFirst()
                .map(s -> new AuditUser(s.getUserId(), s.getTotal(), s.getLastSeen()))
                .orElse(new AuditUser(id, 0, null));
    }

    @SchemaMapping(typeName = "AuditUser")
    public AuditLogPage auditLogs(AuditUser user, @Argument int page, @Argument int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return AuditLogPage.of(repository.findByUserId(user.id(), pageable));
    }

    @BatchMapping(typeName = "AuditLog")
    public Map<AuditLog, AuditUser> user(List<AuditLog> auditLogs) {
        Collection<Long> ids = auditLogs.stream()
                .map(AuditLog::getUserId)
                .collect(Collectors.toSet());

        Map<Long, AuditUser> byId = repository.findStatsByUserIds(ids).stream()
                .collect(Collectors.toMap(
                        UserStatsProjection::getUserId,
                        s -> new AuditUser(s.getUserId(), s.getTotal(), s.getLastSeen())));

        return auditLogs.stream().collect(Collectors.toMap(
                Function.identity(),
                log -> byId.getOrDefault(log.getUserId(),
                        new AuditUser(log.getUserId(), 0, null))));
    }

    @SubscriptionMapping
    public Flux<AuditLog> auditLogAdded() {
        return service.stream();
    }

    private Specification<AuditLog> toSpecification(AuditLogFilter f) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (f != null) {
                if (f.userId() != null)      ps.add(cb.equal(root.get("userId"), f.userId()));
                if (f.commandName() != null) ps.add(cb.equal(root.get("commandName"), f.commandName()));
                if (f.from() != null)        ps.add(cb.greaterThanOrEqualTo(root.<OffsetDateTime>get("timestamp"), f.from()));
                if (f.to() != null)          ps.add(cb.lessThanOrEqualTo(root.<OffsetDateTime>get("timestamp"), f.to()));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
