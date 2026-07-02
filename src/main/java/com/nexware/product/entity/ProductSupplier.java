package com.nexware.product.entity;

import com.nexware.supplier.entity.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "supplier_products")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSupplier {

    @EmbeddedId
    private ProductSupplierId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "supplier_sku", length = 120)
    private String supplierSku;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays;

    @Column(name = "minimum_order_quantity", nullable = false)
    private int minimumOrderQuantity;

    @Column(name = "last_purchase_cost", precision = 14, scale = 2)
    private BigDecimal lastPurchaseCost;

    @Column(nullable = false)
    private boolean preferred;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static ProductSupplier create(Product product, Supplier supplier, boolean preferred) {
        ProductSupplier mapping = new ProductSupplier();
        mapping.id = new ProductSupplierId(supplier.getId(), product.getId());
        mapping.product = product;
        mapping.supplier = supplier;
        mapping.minimumOrderQuantity = 1;
        mapping.leadTimeDays = 0;
        mapping.preferred = preferred;
        return mapping;
    }
}
