package com.nexware.warehouse.dto;

import java.time.Instant;
import java.util.UUID;

public record WarehouseTransferResponse(
        UUID productId,
        String sku,
        UUID sourceWarehouseId,
        UUID destinationWarehouseId,
        int quantity,
        int sourceAvailableAfterTransfer,
        int destinationOnHandAfterTransfer,
        Instant transferredAt
) {
}
