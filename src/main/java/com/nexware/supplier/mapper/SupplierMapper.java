package com.nexware.supplier.mapper;

import com.nexware.supplier.dto.SupplierResponse;
import com.nexware.supplier.entity.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toResponse(Supplier supplier);
}
