package com.nexware.supplier.dto;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String code,
        String legalName,
        String displayName,
        String contactPerson,
        String email,
        String phoneNumber,
        String address,
        String taxIdentifier,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
}
