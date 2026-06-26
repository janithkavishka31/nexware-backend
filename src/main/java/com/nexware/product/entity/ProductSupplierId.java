package com.nexware.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ProductSupplierId(
        @Column(name = "supplier_id")
        UUID supplierId,

        @Column(name = "product_id")
        UUID productId
) implements Serializable {
}
