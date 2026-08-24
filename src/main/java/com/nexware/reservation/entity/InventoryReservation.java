package com.nexware.reservation.entity;

import com.nexware.inventory.entity.Inventory;
import com.nexware.product.entity.Product;
import com.nexware.shared.persistence.AuditableEntity;
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
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Entity
@Table(name = "inventory_reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryReservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by_user_id")
    private User reservedBy;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "reservation_status")
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(name = "reservation_reference", nullable = false, unique = true, length = 120)
    private String reservationReference;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    public static InventoryReservation create(Inventory inventory, User reservedBy, int quantity, String reference, Instant expiresAt) {
        InventoryReservation reservation = new InventoryReservation();
        reservation.inventory = inventory;
        reservation.product = inventory.getProduct();
        reservation.warehouse = inventory.getWarehouse();
        reservation.reservedBy = reservedBy;
        reservation.quantity = quantity;
        reservation.reservationReference = reference;
        reservation.expiresAt = expiresAt;
        reservation.status = ReservationStatus.ACTIVE;
        return reservation;
    }

    public boolean isActive(Instant now) {
        return status == ReservationStatus.ACTIVE && expiresAt.isAfter(now);
    }

    public void release(Instant releasedAt) {
        this.status = ReservationStatus.RELEASED;
        this.releasedAt = releasedAt;
    }

    public void expire(Instant expiredAt) {
        this.status = ReservationStatus.EXPIRED;
        this.releasedAt = expiredAt;
    }
}
