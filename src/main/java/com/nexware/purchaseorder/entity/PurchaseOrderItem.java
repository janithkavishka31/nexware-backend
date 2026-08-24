package com.nexware.purchaseorder.entity;

import com.nexware.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "purchase_order_items")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "ordered_quantity", nullable = false)
    private int orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private int receivedQuantity;

    @Column(name = "unit_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static PurchaseOrderItem create(PurchaseOrder purchaseOrder, Product product, int quantity, BigDecimal unitCost) {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.purchaseOrder = purchaseOrder;
        item.product = product;
        item.orderedQuantity = quantity;
        item.receivedQuantity = 0;
        item.unitCost = unitCost;
        item.lineTotal = unitCost.multiply(BigDecimal.valueOf(quantity));
        return item;
    }

    public void markReceived() {
        this.receivedQuantity = orderedQuantity;
    }
}
