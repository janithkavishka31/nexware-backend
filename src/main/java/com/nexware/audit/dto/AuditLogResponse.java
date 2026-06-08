package com.nexware.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String actorType,
        String action,
        String aggregateType,
        String requestId,
        String ipAddress,
        String userAgent,
        String metadata,
        Instant createdAt
) {
}
