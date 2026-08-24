package com.nexware.reservation.mapper;

import com.nexware.reservation.dto.ReservationResponse;
import com.nexware.reservation.entity.InventoryReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "inventoryId", expression = "java(reservation.getInventory().getId())")
    @Mapping(target = "warehouseId", expression = "java(reservation.getWarehouse().getId())")
    @Mapping(target = "productId", expression = "java(reservation.getProduct().getId())")
    @Mapping(target = "sku", expression = "java(reservation.getProduct().getSku())")
    ReservationResponse toResponse(InventoryReservation reservation);
}
