package com.nexware.warehouse.controller;

import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import com.nexware.warehouse.dto.WarehouseCapacityResponse;
import com.nexware.warehouse.dto.WarehouseCreateRequest;
import com.nexware.warehouse.dto.WarehouseLocationCreateRequest;
import com.nexware.warehouse.dto.WarehouseLocationResponse;
import com.nexware.warehouse.dto.WarehouseLocationUpdateRequest;
import com.nexware.warehouse.dto.WarehouseResponse;
import com.nexware.warehouse.dto.WarehouseSearchCriteria;
import com.nexware.warehouse.dto.WarehouseTransferRequest;
import com.nexware.warehouse.dto.WarehouseTransferResponse;
import com.nexware.warehouse.dto.WarehouseUpdateRequest;
import com.nexware.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${nexware.api.base-path}/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseCreateRequest request
    ) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Warehouse created successfully", response));
    }

    @GetMapping("/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<WarehouseResponse> getWarehouse(@PathVariable UUID warehouseId) {
        return ApiResponse.success(warehouseService.getWarehouse(warehouseId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<WarehouseResponse>> searchWarehouses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        WarehouseSearchCriteria criteria = new WarehouseSearchCriteria(keyword, code, city, active);
        return ApiResponse.success(PageResponse.from(warehouseService.searchWarehouses(criteria, pageable)));
    }

    @PutMapping("/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ApiResponse<WarehouseResponse> updateWarehouse(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody WarehouseUpdateRequest request
    ) {
        return ApiResponse.success("Warehouse updated successfully", warehouseService.updateWarehouse(warehouseId, request));
    }

    @DeleteMapping("/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable UUID warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success("Warehouse deleted successfully", null));
    }

    @GetMapping("/{warehouseId}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<WarehouseCapacityResponse> getCapacity(@PathVariable UUID warehouseId) {
        return ApiResponse.success(warehouseService.getCapacity(warehouseId));
    }

    @PostMapping("/{warehouseId}/locations")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<WarehouseLocationResponse>> createLocation(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody WarehouseLocationCreateRequest request
    ) {
        WarehouseLocationResponse response = warehouseService.createLocation(warehouseId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Warehouse location created successfully", response));
    }

    @GetMapping("/{warehouseId}/locations")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<List<WarehouseLocationResponse>> listLocations(@PathVariable UUID warehouseId) {
        return ApiResponse.success(warehouseService.listLocations(warehouseId));
    }

    @PutMapping("/{warehouseId}/locations/{locationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ApiResponse<WarehouseLocationResponse> updateLocation(
            @PathVariable UUID warehouseId,
            @PathVariable UUID locationId,
            @Valid @RequestBody WarehouseLocationUpdateRequest request
    ) {
        return ApiResponse.success(
                "Warehouse location updated successfully",
                warehouseService.updateLocation(warehouseId, locationId, request)
        );
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<WarehouseTransferResponse> transferStock(@Valid @RequestBody WarehouseTransferRequest request) {
        return ApiResponse.success("Stock transfer completed successfully", warehouseService.transferStock(request));
    }
}
