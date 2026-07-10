package com.nexware.warehouse.entity;

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
@Table(name = "warehouse_locations")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarehouseLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, length = 50)
    private String zone;

    @Column(nullable = false, length = 50)
    private String aisle;

    @Column(nullable = false, length = 50)
    private String rack;

    @Column(nullable = false, length = 50)
    private String bin;

    @Column(name = "capacity_units", nullable = false)
    private int capacityUnits;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static WarehouseLocation create(
            Warehouse warehouse,
            String zone,
            String aisle,
            String rack,
            String bin,
            int capacityUnits
    ) {
        WarehouseLocation location = new WarehouseLocation();
        location.warehouse = warehouse;
        location.zone = zone.toUpperCase();
        location.aisle = aisle.toUpperCase();
        location.rack = rack.toUpperCase();
        location.bin = bin.toUpperCase();
        location.capacityUnits = capacityUnits;
        location.active = true;
        return location;
    }

    public void update(int capacityUnits, boolean active) {
        this.capacityUnits = capacityUnits;
        this.active = active;
    }
}
