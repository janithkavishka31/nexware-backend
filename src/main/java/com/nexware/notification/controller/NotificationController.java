package com.nexware.notification.controller;

import com.nexware.notification.dto.NotificationResponse;
import com.nexware.notification.service.NotificationService;
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
@RequestMapping("${nexware.api.base-path}/notifications")
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<NotificationResponse>> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(PageResponse.from(service.list(pageable)));
    }
}
