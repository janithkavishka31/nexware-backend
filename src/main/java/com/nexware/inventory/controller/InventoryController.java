package com.nexware.inventory.controller;

import com.nexware.inventory.dto.InventoryResponse;
import com.nexware.inventory.dto.InventorySearchCriteria;
import com.nexware.inventory.dto.InventoryValuationResponse;
import com.nexware.inventory.dto.StockAdjustmentRequest;
import com.nexware.inventory.dto.StockMovementResponse;
import com.nexware.inventory.service.InventoryService;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${nexware.api.base-path}/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<InventoryResponse>> searchInventory(
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Boolean lowStockOnly,
            @PageableDefault(size = 20, sort = "updatedAt") Pageable pageable
    ) {
        InventorySearchCriteria criteria = new InventorySearchCriteria(warehouseId, productId, sku, lowStockOnly);
        return ApiResponse.success(PageResponse.from(inventoryService.searchInventory(criteria, pageable)));
    }

    @GetMapping("/warehouses/{warehouseId}/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<InventoryResponse> getInventory(
            @PathVariable UUID warehouseId,
            @PathVariable UUID productId
    ) {
        return ApiResponse.success(inventoryService.getInventory(warehouseId, productId));
    }

    @PatchMapping("/adjustments")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<InventoryResponse> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        return ApiResponse.success("Stock adjusted successfully", inventoryService.adjustStock(request));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<InventoryResponse>> lowStock(
            @PageableDefault(size = 20, sort = "updatedAt") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(inventoryService.lowStock(pageable)));
    }

    @GetMapping("/valuation")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_OFFICER')")
    public ApiResponse<InventoryValuationResponse> valuation() {
        return ApiResponse.success(inventoryService.valuation());
    }

    @GetMapping("/products/{productId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<StockMovementResponse>> productHistory(
            @PathVariable UUID productId,
            @PageableDefault(size = 20, sort = "occurredAt") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(inventoryService.getProductHistory(productId, pageable)));
    }

    @GetMapping("/warehouses/{warehouseId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<StockMovementResponse>> warehouseHistory(
            @PathVariable UUID warehouseId,
            @PageableDefault(size = 20, sort = "occurredAt") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(inventoryService.getWarehouseHistory(warehouseId, pageable)));
    }
}
