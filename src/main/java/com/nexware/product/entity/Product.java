package com.nexware.product.entity;

import com.nexware.shared.persistence.SoftDeletableEntity;
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
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(nullable = false, unique = true, length = 80)
    private String sku;

    @Column(unique = true, length = 128)
    private String barcode;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 120)
    private String brand;

    @Column(length = 120)
    private String model;

    @Column(name = "unit_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "low_stock_threshold", nullable = false)
    private int lowStockThreshold;

    @Column(name = "track_serial_numbers", nullable = false)
    private boolean trackSerialNumbers;

    @Column(nullable = false)
    private boolean active = true;

    public static Product create(
            ProductCategory category,
            String sku,
            String barcode,
            String name,
            String description,
            String brand,
            String model,
            BigDecimal unitCost,
            BigDecimal unitPrice,
            int lowStockThreshold,
            boolean trackSerialNumbers
    ) {
        Product product = new Product();
        product.category = category;
        product.sku = sku.toUpperCase();
        product.barcode = barcode;
        product.name = name;
        product.description = description;
        product.brand = brand;
        product.model = model;
        product.unitCost = unitCost;
        product.unitPrice = unitPrice;
        product.lowStockThreshold = lowStockThreshold;
        product.trackSerialNumbers = trackSerialNumbers;
        product.active = true;
        return product;
    }

    public void update(
            ProductCategory category,
            String barcode,
            String name,
            String description,
            String brand,
            String model,
            BigDecimal unitCost,
            BigDecimal unitPrice,
            int lowStockThreshold,
            boolean trackSerialNumbers,
            boolean active
    ) {
        this.category = category;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.lowStockThreshold = lowStockThreshold;
        this.trackSerialNumbers = trackSerialNumbers;
        this.active = active;
    }

    public void softDelete(Instant deletedAt) {
        this.active = false;
        markDeleted(deletedAt);
    }
}
