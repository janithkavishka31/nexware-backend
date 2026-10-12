package com.nexware.inventory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void inventoryValueUsesAverageCostAndQuantityOnHand() {
        Inventory inventory = Inventory.empty(null, null, BigDecimal.valueOf(25));

        inventory.receive(4, BigDecimal.valueOf(25));

        assertThat(inventory.inventoryValue()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(inventory.availableQuantity()).isEqualTo(4);
    }
}
