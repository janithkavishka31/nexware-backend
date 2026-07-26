package com.nexware.inventory.repository;

import com.nexware.inventory.dto.InventorySearchCriteria;
import com.nexware.inventory.entity.Inventory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class InventorySpecifications {

    private InventorySpecifications() {
    }

    public static Specification<Inventory> matching(InventorySearchCriteria criteria) {
        return Specification
                .where(warehouse(criteria.warehouseId()))
                .and(product(criteria.productId()))
                .and(sku(criteria.sku()))
                .and(lowStock(criteria.lowStockOnly()));
    }

    private static Specification<Inventory> warehouse(Object warehouseId) {
        if (warehouseId == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("warehouse").get("id"), warehouseId);
    }

    private static Specification<Inventory> product(Object productId) {
        if (productId == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("product").get("id"), productId);
    }

    private static Specification<Inventory> sku(String sku) {
        if (!StringUtils.hasText(sku)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("product").get("sku"), sku.trim().toUpperCase());
    }

    private static Specification<Inventory> lowStock(Boolean lowStockOnly) {
        if (!Boolean.TRUE.equals(lowStockOnly)) {
            return null;
        }
        return (root, query, builder) -> builder.and(
                builder.greaterThan(root.get("product").get("lowStockThreshold"), 0),
                builder.lessThanOrEqualTo(
                        builder.diff(root.<Integer>get("quantityOnHand"), root.<Integer>get("quantityReserved")),
                        root.get("product").<Integer>get("lowStockThreshold")
                )
        );
    }
}
