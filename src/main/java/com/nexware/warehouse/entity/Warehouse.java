package com.nexware.warehouse.entity;

import com.nexware.shared.persistence.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "warehouses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 32)
    private String postalCode;

    @Column(nullable = false, length = 100)
    private String country = "Sri Lanka";

    @Column(name = "capacity_units", nullable = false)
    private int capacityUnits;

    @Column(nullable = false)
    private boolean active = true;

    public static Warehouse create(
            String code,
            String name,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            int capacityUnits
    ) {
        Warehouse warehouse = new Warehouse();
        warehouse.code = code.toUpperCase();
        warehouse.name = name;
        warehouse.addressLine1 = addressLine1;
        warehouse.addressLine2 = addressLine2;
        warehouse.city = city;
        warehouse.state = state;
        warehouse.postalCode = postalCode;
        warehouse.country = country;
        warehouse.capacityUnits = capacityUnits;
        warehouse.active = true;
        return warehouse;
    }

    public void update(
            String name,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            int capacityUnits,
            boolean active
    ) {
        this.name = name;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.capacityUnits = capacityUnits;
        this.active = active;
    }

    public void softDelete(Instant deletedAt) {
        this.active = false;
        markDeleted(deletedAt);
    }
}
