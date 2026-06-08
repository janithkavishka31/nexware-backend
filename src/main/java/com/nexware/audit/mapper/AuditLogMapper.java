package com.nexware.audit.mapper;

import com.nexware.audit.dto.AuditLogResponse;
import com.nexware.audit.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}
