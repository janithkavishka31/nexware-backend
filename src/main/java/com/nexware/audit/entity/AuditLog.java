package com.nexware.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "audit_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "actor_type", nullable = false, length = 40)
    private String actorType;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(name = "aggregate_type", nullable = false, length = 120)
    private String aggregateType;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "request_id", length = 120)
    private String requestId;

    @Column(name = "ip_address")
    @JdbcTypeCode(SqlTypes.INET)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private String metadata = "{}";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static AuditLog request(String actor, String method, String uri, String requestId, String ipAddress, String userAgent, Instant now) {
        AuditLog log = new AuditLog();
        log.actorType = actor == null ? "ANONYMOUS" : "USER";
        log.action = method;
        log.aggregateType = uri.length() > 120 ? uri.substring(0, 120) : uri;
        log.requestId = requestId;
        log.ipAddress = ipAddress;
        log.userAgent = userAgent;
        log.metadata = "{\"actor\":\"" + (actor == null ? "anonymous" : actor.replace("\"", "'")) + "\"}";
        log.createdAt = now;
        return log;
    }
}
