package com.nexware.product.controller;

import com.nexware.product.dto.ProductCategoryCreateRequest;
import com.nexware.product.dto.ProductCategoryResponse;
import com.nexware.product.dto.ProductCreateRequest;
import com.nexware.product.dto.ProductResponse;
import com.nexware.product.dto.ProductSearchCriteria;
import com.nexware.product.dto.ProductSerialNumberResponse;
import com.nexware.product.dto.ProductUpdateRequest;
import com.nexware.product.dto.SerialNumberCreateRequest;
import com.nexware.product.service.ProductService;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
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
@RequestMapping("${nexware.api.base-path}/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> createCategory(
            @Valid @RequestBody ProductCategoryCreateRequest request
    ) {
        ProductCategoryResponse response = productService.createCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product category created successfully", response));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<List<ProductCategoryResponse>> listCategories() {
        return ApiResponse.success(productService.listCategories());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ApiResponse.success(productService.getProduct(productId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF', 'PROCUREMENT_OFFICER')")
    public ApiResponse<PageResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean trackSerialNumbers,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                keyword,
                categoryId,
                sku,
                barcode,
                active,
                trackSerialNumbers
        );
        return ApiResponse.success(PageResponse.from(productService.searchProducts(criteria, pageable)));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ApiResponse.success("Product updated successfully", productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @PostMapping("/{productId}/serial-numbers")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ResponseEntity<ApiResponse<ProductSerialNumberResponse>> addSerialNumber(
            @PathVariable UUID productId,
            @Valid @RequestBody SerialNumberCreateRequest request
    ) {
        ProductSerialNumberResponse response = productService.addSerialNumber(productId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Serial number registered successfully", response));
    }

    @GetMapping("/{productId}/serial-numbers")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'INVENTORY_STAFF')")
    public ApiResponse<List<ProductSerialNumberResponse>> listSerialNumbers(@PathVariable UUID productId) {
        return ApiResponse.success(productService.listSerialNumbers(productId));
    }
}
