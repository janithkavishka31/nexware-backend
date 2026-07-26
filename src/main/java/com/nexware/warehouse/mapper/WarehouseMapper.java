package com.nexware.warehouse.mapper;

import com.nexware.warehouse.dto.WarehouseLocationResponse;
import com.nexware.warehouse.dto.WarehouseResponse;
import com.nexware.warehouse.entity.Warehouse;
import com.nexware.warehouse.entity.WarehouseLocation;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseResponse toWarehouseResponse(Warehouse warehouse);

    @Mapping(target = "warehouseId", expression = "java(warehouseId(location))")
    WarehouseLocationResponse toLocationResponse(WarehouseLocation location);

    default UUID warehouseId(WarehouseLocation location) {
        return location.getWarehouse().getId();
    }
}
