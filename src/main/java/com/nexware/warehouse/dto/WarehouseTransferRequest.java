package com.nexware.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record WarehouseTransferRequest(
        @NotNull
        UUID productId,

        @NotNull
        UUID sourceWarehouseId,

        @NotNull
        UUID destinationWarehouseId,

        @Min(1)
        int quantity,

        @Size(max = 500)
        String reason
) {
}
