package com.nexware.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseLocationCreateRequest(
        @NotBlank
        @Size(max = 50)
        String zone,

        @NotBlank
        @Size(max = 50)
        String aisle,

        @NotBlank
        @Size(max = 50)
        String rack,

        @NotBlank
        @Size(max = 50)
        String bin,

        @Min(1)
        int capacityUnits
) {
}
