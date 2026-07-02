package com.nexware.product.repository;

import com.nexware.product.dto.ProductSearchCriteria;
import com.nexware.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> matching(ProductSearchCriteria criteria) {
        return Specification
                .where(notDeleted())
                .and(keyword(criteria.keyword()))
                .and(category(criteria.categoryId()))
                .and(sku(criteria.sku()))
                .and(barcode(criteria.barcode()))
                .and(active(criteria.active()))
                .and(trackSerialNumbers(criteria.trackSerialNumbers()));
    }

    private static Specification<Product> notDeleted() {
        return (root, query, builder) -> builder.isNull(root.get("deletedAt"));
    }

    private static Specification<Product> keyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("name")), pattern),
                builder.like(builder.lower(root.get("description")), pattern),
                builder.like(builder.lower(root.get("brand")), pattern),
                builder.like(builder.lower(root.get("model")), pattern),
                builder.like(builder.lower(root.get("sku")), pattern)
        );
    }

    private static Specification<Product> category(Object categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> sku(String sku) {
        if (!StringUtils.hasText(sku)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("sku"), sku.trim().toUpperCase());
    }

    private static Specification<Product> barcode(String barcode) {
        if (!StringUtils.hasText(barcode)) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("barcode"), barcode.trim());
    }

    private static Specification<Product> active(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }

    private static Specification<Product> trackSerialNumbers(Boolean trackSerialNumbers) {
        if (trackSerialNumbers == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("trackSerialNumbers"), trackSerialNumbers);
    }
}
