package com.nexware.warehouse.repository;

import com.nexware.warehouse.dto.WarehouseSearchCriteria;
import com.nexware.warehouse.entity.Warehouse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class WarehouseSpecifications {

    private WarehouseSpecifications() {
    }

    public static Specification<Warehouse> matching(WarehouseSearchCriteria criteria) {
        return Specification
                .where(notDeleted())
                .and(keyword(criteria.keyword()))
                .and(code(criteria.code()))
                .and(city(criteria.city()))
                .and(active(criteria.active()));
    }

    private static Specification<Warehouse> notDeleted() {
        return (root, query, builder) -> builder.isNull(root.get("deletedAt"));
    }

    private static Specification<Warehouse> keyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("name")), pattern),
                builder.like(builder.lower(root.get("code")), pattern),
                builder.like(builder.lower(root.get("city")), pattern),
                builder.like(builder.lower(root.get("country")), pattern)
        );
    }

    private static Specification<Warehouse> code(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("code"), code.trim().toUpperCase());
    }

    private static Specification<Warehouse> city(String city) {
        if (!StringUtils.hasText(city)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(builder.lower(root.get("city")), city.trim().toLowerCase());
    }

    private static Specification<Warehouse> active(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }
}
