package com.nexware.purchaseorder.mapper;

import com.nexware.purchaseorder.dto.PurchaseOrderItemResponse;
import com.nexware.purchaseorder.dto.PurchaseOrderResponse;
import com.nexware.purchaseorder.entity.PurchaseOrder;
import com.nexware.purchaseorder.entity.PurchaseOrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    @Mapping(target = "supplierId", expression = "java(order.getSupplier().getId())")
    @Mapping(target = "supplierName", expression = "java(order.getSupplier().getDisplayName())")
    PurchaseOrderResponse toResponse(PurchaseOrder order, List<PurchaseOrderItemResponse> items);

    @Mapping(target = "productId", expression = "java(item.getProduct().getId())")
    @Mapping(target = "sku", expression = "java(item.getProduct().getSku())")
    PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item);
}
