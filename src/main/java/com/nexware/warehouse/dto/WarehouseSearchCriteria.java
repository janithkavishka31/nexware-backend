package com.nexware.warehouse.dto;

public record WarehouseSearchCriteria(
        String keyword,
        String code,
        String city,
        Boolean active
) {
}
