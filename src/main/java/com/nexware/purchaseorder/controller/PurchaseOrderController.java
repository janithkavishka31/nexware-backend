package com.nexware.purchaseorder.controller;

import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.purchaseorder.dto.PurchaseOrderCreateRequest;
import com.nexware.purchaseorder.dto.PurchaseOrderResponse;
import com.nexware.purchaseorder.dto.ReceivePurchaseOrderRequest;
import com.nexware.purchaseorder.dto.RejectPurchaseOrderRequest;
import com.nexware.purchaseorder.entity.PurchaseOrderStatus;
import com.nexware.purchaseorder.service.PurchaseOrderService;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("${nexware.api.base-path}/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> create(@Valid @RequestBody PurchaseOrderCreateRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Purchase order created successfully", service.create(request, principal)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'WAREHOUSE_MANAGER')")
    public ApiResponse<PageResponse<PurchaseOrderResponse>> list(@RequestParam(required = false) PurchaseOrderStatus status, @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(PageResponse.from(service.list(status, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'WAREHOUSE_MANAGER')")
    public ApiResponse<PurchaseOrderResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PurchaseOrderResponse> approve(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success("Purchase order approved", service.approve(id, principal));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PurchaseOrderResponse> reject(@PathVariable UUID id, @Valid @RequestBody RejectPurchaseOrderRequest request) {
        return ApiResponse.success("Purchase order rejected", service.reject(id, request));
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PurchaseOrderResponse> receive(@PathVariable UUID id, @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        return ApiResponse.success("Purchase order received", service.receive(id, request));
    }
}
