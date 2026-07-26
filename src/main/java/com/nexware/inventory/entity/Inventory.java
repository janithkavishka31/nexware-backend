package com.nexware.inventory.entity;

import com.nexware.product.entity.Product;
import com.nexware.shared.persistence.AuditableEntity;
import com.nexware.warehouse.entity.Warehouse;
import com.nexware.warehouse.entity.WarehouseLocation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "average_unit_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal averageUnitCost = BigDecimal.ZERO;

    public static Inventory empty(Warehouse warehouse, Product product, BigDecimal averageUnitCost) {
        return empty(warehouse, product, null, averageUnitCost);
    }

    public static Inventory empty(
            Warehouse warehouse,
            Product product,
            WarehouseLocation location,
            BigDecimal averageUnitCost
    ) {
        Inventory inventory = new Inventory();
        inventory.warehouse = warehouse;
        inventory.product = product;
        inventory.location = location;
        inventory.quantityOnHand = 0;
        inventory.quantityReserved = 0;
        inventory.averageUnitCost = averageUnitCost == null ? BigDecimal.ZERO : averageUnitCost;
        return inventory;
    }

    public int availableQuantity() {
        return quantityOnHand - quantityReserved;
    }

    public void transferOut(int quantity) {
        this.quantityOnHand -= quantity;
    }

    public void transferIn(int quantity, BigDecimal unitCost) {
        this.quantityOnHand += quantity;
        if (unitCost != null && unitCost.signum() >= 0) {
            this.averageUnitCost = unitCost;
        }
    }

    public void adjustTo(int newQuantityOnHand, BigDecimal unitCost, WarehouseLocation location) {
        this.quantityOnHand = newQuantityOnHand;
        this.location = location;
        if (unitCost != null && unitCost.signum() >= 0) {
            this.averageUnitCost = unitCost;
        }
    }

    public BigDecimal inventoryValue() {
        return averageUnitCost.multiply(BigDecimal.valueOf(quantityOnHand));
    }

    public void reserve(int quantity) {
        this.quantityReserved += quantity;
    }

    public void releaseReservation(int quantity) {
        this.quantityReserved -= quantity;
    }

    public void receive(int quantity, BigDecimal unitCost) {
        this.quantityOnHand += quantity;
        if (unitCost != null && unitCost.signum() >= 0) {
            this.averageUnitCost = unitCost;
        }
    }
}
