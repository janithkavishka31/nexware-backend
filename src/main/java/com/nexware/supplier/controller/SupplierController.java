package com.nexware.supplier.controller;

import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import com.nexware.supplier.dto.SupplierCreateRequest;
import com.nexware.supplier.dto.SupplierResponse;
import com.nexware.supplier.dto.SupplierSearchCriteria;
import com.nexware.supplier.dto.SupplierUpdateRequest;
import com.nexware.supplier.service.SupplierService;
import jakarta.validation.Valid;
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
@RequestMapping("${nexware.api.base-path}/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody SupplierCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Supplier created successfully", supplierService.create(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'WAREHOUSE_MANAGER')")
    public ApiResponse<SupplierResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(supplierService.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'WAREHOUSE_MANAGER')")
    public ApiResponse<PageResponse<SupplierResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "displayName") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(supplierService.search(new SupplierSearchCriteria(keyword, code, active), pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ApiResponse<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierUpdateRequest request) {
        return ApiResponse.success("Supplier updated successfully", supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        supplierService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully", null));
    }
}
