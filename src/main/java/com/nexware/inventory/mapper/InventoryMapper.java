package com.nexware.inventory.mapper;

import com.nexware.inventory.dto.InventoryResponse;
import com.nexware.inventory.dto.StockMovementResponse;
import com.nexware.inventory.entity.Inventory;
import com.nexware.inventory.entity.StockMovement;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "warehouseId", expression = "java(inventory.getWarehouse().getId())")
    @Mapping(target = "warehouseCode", expression = "java(inventory.getWarehouse().getCode())")
    @Mapping(target = "productId", expression = "java(inventory.getProduct().getId())")
    @Mapping(target = "sku", expression = "java(inventory.getProduct().getSku())")
    @Mapping(target = "productName", expression = "java(inventory.getProduct().getName())")
    @Mapping(target = "locationId", expression = "java(locationId(inventory))")
    @Mapping(target = "quantityAvailable", expression = "java(inventory.availableQuantity())")
    @Mapping(target = "inventoryValue", expression = "java(inventory.inventoryValue())")
    @Mapping(target = "lowStock", expression = "java(isLowStock(inventory))")
    InventoryResponse toInventoryResponse(Inventory inventory);

    @Mapping(target = "productId", expression = "java(movement.getProduct().getId())")
    @Mapping(target = "sku", expression = "java(movement.getProduct().getSku())")
    @Mapping(target = "sourceWarehouseId", expression = "java(warehouseId(movement.getSourceWarehouse()))")
    @Mapping(target = "destinationWarehouseId", expression = "java(warehouseId(movement.getDestinationWarehouse()))")
    StockMovementResponse toMovementResponse(StockMovement movement);

    default UUID locationId(Inventory inventory) {
        return inventory.getLocation() == null ? null : inventory.getLocation().getId();
    }

    default UUID warehouseId(com.nexware.warehouse.entity.Warehouse warehouse) {
        return warehouse == null ? null : warehouse.getId();
    }

    default boolean isLowStock(Inventory inventory) {
        int threshold = inventory.getProduct().getLowStockThreshold();
        return threshold > 0 && inventory.availableQuantity() <= threshold;
    }
}
