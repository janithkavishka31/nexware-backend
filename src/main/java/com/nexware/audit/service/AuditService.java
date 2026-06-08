package com.nexware.audit.service;

import com.nexware.audit.dto.AuditLogResponse;
import com.nexware.audit.entity.AuditLog;
import com.nexware.audit.mapper.AuditLogMapper;
import com.nexware.audit.repository.AuditLogRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;
    private final AuditLogMapper mapper;
    private final Clock clock = Clock.systemUTC();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordRequest(String actor, String method, String uri, String requestId, String ipAddress, String userAgent) {
        repository.save(AuditLog.request(actor, method, uri, requestId, ipAddress, userAgent, Instant.now(clock)));
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> list(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable).map(mapper::toResponse);
    }
}
