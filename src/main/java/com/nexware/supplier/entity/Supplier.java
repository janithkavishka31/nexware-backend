package com.nexware.supplier.entity;

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
@Table(name = "suppliers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(name = "legal_name", nullable = false, length = 180)
    private String legalName;

    @Column(name = "display_name", nullable = false, length = 180)
    private String displayName;

    @Column(name = "contact_person", length = 140)
    private String contactPerson;

    @Column(length = 255)
    private String email;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    @Column(length = 500)
    private String address;

    @Column(name = "tax_identifier", length = 80)
    private String taxIdentifier;

    @Column(nullable = false)
    private boolean active = true;

    public static Supplier create(
            String code,
            String legalName,
            String displayName,
            String contactPerson,
            String email,
            String phoneNumber,
            String address,
            String taxIdentifier
    ) {
        Supplier supplier = new Supplier();
        supplier.code = code.toUpperCase();
        supplier.legalName = legalName;
        supplier.displayName = displayName;
        supplier.contactPerson = contactPerson;
        supplier.email = email;
        supplier.phoneNumber = phoneNumber;
        supplier.address = address;
        supplier.taxIdentifier = taxIdentifier;
        supplier.active = true;
        return supplier;
    }

    public void update(
            String legalName,
            String displayName,
            String contactPerson,
            String email,
            String phoneNumber,
            String address,
            String taxIdentifier,
            boolean active
    ) {
        this.legalName = legalName;
        this.displayName = displayName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.taxIdentifier = taxIdentifier;
        this.active = active;
    }

    public void softDelete(Instant deletedAt) {
        this.active = false;
        markDeleted(deletedAt);
    }
}
