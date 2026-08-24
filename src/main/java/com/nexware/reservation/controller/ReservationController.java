package com.nexware.reservation.controller;

import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.reservation.dto.ReservationCreateRequest;
import com.nexware.reservation.dto.ReservationResponse;
import com.nexware.reservation.service.ReservationService;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${nexware.api.base-path}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<ReservationResponse> reserve(
            @Valid @RequestBody ReservationCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ApiResponse.success("Inventory reserved successfully", reservationService.reserve(request, principal));
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<ReservationResponse> get(@PathVariable UUID reservationId) {
        return ApiResponse.success(reservationService.get(reservationId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<PageResponse<ReservationResponse>> list(
            @RequestParam(required = false) UUID warehouseId,
            @PageableDefault(size = 20, sort = "updatedAt") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(reservationService.list(warehouseId, pageable)));
    }

    @PatchMapping("/{reservationId}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<ReservationResponse> release(@PathVariable UUID reservationId) {
        return ApiResponse.success("Reservation released successfully", reservationService.release(reservationId));
    }

    @PostMapping("/expire-due")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> expireDueReservations() {
        return ApiResponse.success("Due reservations expired", reservationService.expireDueReservations());
    }
}
