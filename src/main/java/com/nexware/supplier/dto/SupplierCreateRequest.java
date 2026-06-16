package com.nexware.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierCreateRequest(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 180) String legalName,
        @NotBlank @Size(max = 180) String displayName,
        @Size(max = 140) String contactPerson,
        @Email @Size(max = 255) String email,
        @Size(max = 32) String phoneNumber,
        @Size(max = 500) String address,
        @Size(max = 80) String taxIdentifier
) {
}
