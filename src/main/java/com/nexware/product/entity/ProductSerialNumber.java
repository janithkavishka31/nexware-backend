package com.nexware.product.entity;

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
@Table(name = "product_serial_numbers")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSerialNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "serial_number", nullable = false, unique = true, length = 160)
    private String serialNumber;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Column(name = "inventory_status", nullable = false, length = 40)
    private String inventoryStatus = "AVAILABLE";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static ProductSerialNumber create(Product product, String serialNumber, UUID warehouseId) {
        ProductSerialNumber item = new ProductSerialNumber();
        item.product = product;
        item.serialNumber = serialNumber;
        item.warehouseId = warehouseId;
        item.inventoryStatus = "AVAILABLE";
        return item;
    }
}
