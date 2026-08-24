package com.nexware.purchaseorder.entity;

import com.nexware.shared.persistence.AuditableEntity;
import com.nexware.supplier.entity.Supplier;
import com.nexware.user.entity.User;
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
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Entity
@Table(name = "purchase_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "order_number", nullable = false, unique = true, length = 80)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "purchase_order_status")
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "rejected_reason", length = 500)
    private String rejectedReason;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    public static PurchaseOrder draft(Supplier supplier, String orderNumber, LocalDate expectedDeliveryDate, String notes, User createdBy) {
        PurchaseOrder po = new PurchaseOrder();
        po.supplier = supplier;
        po.orderNumber = orderNumber;
        po.expectedDeliveryDate = expectedDeliveryDate;
        po.notes = notes;
        po.createdBy = createdBy;
        po.status = PurchaseOrderStatus.DRAFT;
        return po;
    }

    public void updateTotals(BigDecimal subtotal, BigDecimal taxTotal) {
        this.subtotal = subtotal;
        this.taxTotal = taxTotal;
        this.grandTotal = subtotal.add(taxTotal);
    }

    public void approve(User approver, Instant approvedAt) {
        this.status = PurchaseOrderStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = approvedAt;
    }

    public void reject(String reason) {
        this.status = PurchaseOrderStatus.REJECTED;
        this.rejectedReason = reason;
    }

    public void receive(Instant receivedAt) {
        this.status = PurchaseOrderStatus.RECEIVED;
        this.receivedAt = receivedAt;
    }
}
