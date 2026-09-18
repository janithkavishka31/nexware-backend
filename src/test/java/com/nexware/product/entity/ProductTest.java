package com.nexware.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void createNormalizesSkuAndKeepsCatalogFields() {
        ProductCategory category = ProductCategory.create("LAPTOP", "Laptops", null, null);

        Product product = Product.create(
                category,
                "sku-001",
                "1234567890",
                "Business Laptop",
                "Enterprise laptop",
                "NexBrand",
                "NB-14",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(650),
                5,
                true
        );

        assertThat(product.getSku()).isEqualTo("SKU-001");
        assertThat(product.isActive()).isTrue();
        assertThat(product.isTrackSerialNumbers()).isTrue();
    }
}
