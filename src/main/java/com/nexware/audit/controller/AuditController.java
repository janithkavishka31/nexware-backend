package com.nexware.audit.controller;

import com.nexware.audit.dto.AuditLogResponse;
import com.nexware.audit.service.AuditService;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${nexware.api.base-path}/audit")
public class AuditController {

    private final AuditService service;

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<AuditLogResponse>> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(PageResponse.from(service.list(pageable)));
    }
}
