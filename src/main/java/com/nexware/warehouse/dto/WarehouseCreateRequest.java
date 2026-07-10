package com.nexware.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseCreateRequest(
        @NotBlank
        @Size(max = 40)
        String code,

        @NotBlank
        @Size(max = 160)
        String name,

        @NotBlank
        @Size(max = 255)
        String addressLine1,

        @Size(max = 255)
        String addressLine2,

        @NotBlank
        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @Size(max = 32)
        String postalCode,

        @NotBlank
        @Size(max = 100)
        String country,

        @Min(1)
        int capacityUnits
) {
}
