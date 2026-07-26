package com.nexware.inventory.entity;

import com.nexware.product.entity.Product;
import com.nexware.user.entity.User;
import com.nexware.warehouse.entity.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Entity
@Table(name = "stock_movements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "movement_type", nullable = false, columnDefinition = "stock_movement_type")
    private StockMovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_cost", precision = 14, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "reference_type", length = 80)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(length = 500)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_user_id")
    private User performedBy;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static StockMovement transferOut(
            Product product,
            Warehouse sourceWarehouse,
            Warehouse destinationWarehouse,
            int quantity,
            BigDecimal unitCost,
            String reason,
            Instant occurredAt
    ) {
        return transfer(product, sourceWarehouse, destinationWarehouse, StockMovementType.TRANSFER_OUT, quantity, unitCost, reason, occurredAt);
    }

    public static StockMovement transferIn(
            Product product,
            Warehouse sourceWarehouse,
            Warehouse destinationWarehouse,
            int quantity,
            BigDecimal unitCost,
            String reason,
            Instant occurredAt
    ) {
        return transfer(product, sourceWarehouse, destinationWarehouse, StockMovementType.TRANSFER_IN, quantity, unitCost, reason, occurredAt);
    }

    public static StockMovement adjustment(
            Product product,
            Warehouse warehouse,
            int quantityDelta,
            BigDecimal unitCost,
            String reason,
            Instant occurredAt
    ) {
        StockMovement movement = new StockMovement();
        movement.product = product;
        movement.sourceWarehouse = warehouse;
        movement.destinationWarehouse = warehouse;
        movement.movementType = StockMovementType.ADJUSTMENT;
        movement.quantity = Math.abs(quantityDelta);
        movement.unitCost = unitCost;
        movement.referenceType = "STOCK_ADJUSTMENT";
        movement.reason = reason;
        movement.occurredAt = occurredAt;
        movement.createdAt = occurredAt;
        return movement;
    }

    public static StockMovement reservation(
            Product product,
            Warehouse warehouse,
            int quantity,
            String referenceType,
            UUID referenceId,
            StockMovementType movementType,
            String reason,
            Instant occurredAt
    ) {
        StockMovement movement = new StockMovement();
        movement.product = product;
        movement.sourceWarehouse = warehouse;
        movement.destinationWarehouse = warehouse;
        movement.movementType = movementType;
        movement.quantity = quantity;
        movement.referenceType = referenceType;
        movement.referenceId = referenceId;
        movement.reason = reason;
        movement.occurredAt = occurredAt;
        movement.createdAt = occurredAt;
        return movement;
    }

    public static StockMovement purchaseReceipt(
            Product product,
            Warehouse warehouse,
            int quantity,
            BigDecimal unitCost,
            UUID purchaseOrderId,
            Instant occurredAt
    ) {
        StockMovement movement = new StockMovement();
        movement.product = product;
        movement.destinationWarehouse = warehouse;
        movement.movementType = StockMovementType.PURCHASE_RECEIPT;
        movement.quantity = quantity;
        movement.unitCost = unitCost;
        movement.referenceType = "PURCHASE_ORDER";
        movement.referenceId = purchaseOrderId;
        movement.reason = "Purchase order received";
        movement.occurredAt = occurredAt;
        movement.createdAt = occurredAt;
        return movement;
    }

    private static StockMovement transfer(
            Product product,
            Warehouse sourceWarehouse,
            Warehouse destinationWarehouse,
            StockMovementType movementType,
            int quantity,
            BigDecimal unitCost,
            String reason,
            Instant occurredAt
    ) {
        StockMovement movement = new StockMovement();
        movement.product = product;
        movement.sourceWarehouse = sourceWarehouse;
        movement.destinationWarehouse = destinationWarehouse;
        movement.movementType = movementType;
        movement.quantity = quantity;
        movement.unitCost = unitCost;
        movement.referenceType = "WAREHOUSE_TRANSFER";
        movement.reason = reason;
        movement.occurredAt = occurredAt;
        movement.createdAt = occurredAt;
        return movement;
    }
}
