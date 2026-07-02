package com.nexware.supplier.service;

import com.nexware.shared.exception.ConflictException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.supplier.dto.SupplierCreateRequest;
import com.nexware.supplier.dto.SupplierResponse;
import com.nexware.supplier.dto.SupplierSearchCriteria;
import com.nexware.supplier.dto.SupplierUpdateRequest;
import com.nexware.supplier.entity.Supplier;
import com.nexware.supplier.mapper.SupplierMapper;
import com.nexware.supplier.repository.SupplierRepository;
import com.nexware.supplier.repository.SupplierSpecifications;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public SupplierResponse create(SupplierCreateRequest request) {
        String code = request.code().trim().toUpperCase();
        supplierRepository.findByCodeAndDeletedAtIsNull(code).ifPresent(existing -> {
            throw new ConflictException("Supplier code already exists");
        });
        Supplier supplier = Supplier.create(code, request.legalName().trim(), request.displayName().trim(), trimToNull(request.contactPerson()),
                trimToNull(request.email()), trimToNull(request.phoneNumber()), trimToNull(request.address()), trimToNull(request.taxIdentifier()));
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional(readOnly = true)
    public SupplierResponse get(UUID id) {
        return supplierRepository.findByIdAndDeletedAtIsNull(id).map(supplierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> search(SupplierSearchCriteria criteria, Pageable pageable) {
        return supplierRepository.findAll(SupplierSpecifications.matching(criteria), pageable).map(supplierMapper::toResponse);
    }

    @Transactional
    public SupplierResponse update(UUID id, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        supplier.update(request.legalName().trim(), request.displayName().trim(), trimToNull(request.contactPerson()),
                trimToNull(request.email()), trimToNull(request.phoneNumber()), trimToNull(request.address()), trimToNull(request.taxIdentifier()), request.active());
        return supplierMapper.toResponse(supplier);
    }

    @Transactional
    public void delete(UUID id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        supplier.softDelete(Instant.now(clock));
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
