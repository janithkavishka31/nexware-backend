package com.nexware.supplier.repository;

import com.nexware.supplier.dto.SupplierSearchCriteria;
import com.nexware.supplier.entity.Supplier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class SupplierSpecifications {

    private SupplierSpecifications() {
    }

    public static Specification<Supplier> matching(SupplierSearchCriteria criteria) {
        return Specification.where(notDeleted()).and(keyword(criteria.keyword())).and(code(criteria.code())).and(active(criteria.active()));
    }

    private static Specification<Supplier> notDeleted() {
        return (root, query, builder) -> builder.isNull(root.get("deletedAt"));
    }

    private static Specification<Supplier> keyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("legalName")), pattern),
                builder.like(builder.lower(root.get("displayName")), pattern),
                builder.like(builder.lower(root.get("contactPerson")), pattern)
        );
    }

    private static Specification<Supplier> code(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("code"), code.trim().toUpperCase());
    }

    private static Specification<Supplier> active(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }
}
