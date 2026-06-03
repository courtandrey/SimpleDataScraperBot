package com.github.courtandrey.simpledatascraperbot.audit;

import java.time.OffsetDateTime;

public interface UserStatsProjection {
    Long getUserId();
    long getTotal();
    OffsetDateTime getLastSeen();
}
