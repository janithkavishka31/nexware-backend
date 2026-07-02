package com.nexware.product.mapper;

import com.nexware.product.dto.ProductCategoryResponse;
import com.nexware.product.dto.ProductResponse;
import com.nexware.product.dto.ProductSerialNumberResponse;
import com.nexware.product.dto.SupplierSummaryResponse;
import com.nexware.product.entity.Product;
import com.nexware.product.entity.ProductCategory;
import com.nexware.product.entity.ProductSerialNumber;
import com.nexware.product.entity.ProductSupplier;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "parentId", expression = "java(parentId(category))")
    ProductCategoryResponse toCategoryResponse(ProductCategory category);

    @Mapping(target = "category", source = "product.category")
    ProductResponse toProductResponse(Product product, List<SupplierSummaryResponse> suppliers);

    ProductSerialNumberResponse toSerialNumberResponse(ProductSerialNumber serialNumber);

    default SupplierSummaryResponse toSupplierSummary(ProductSupplier mapping) {
        return new SupplierSummaryResponse(
                mapping.getSupplier().getId(),
                mapping.getSupplier().getCode(),
                mapping.getSupplier().getDisplayName(),
                mapping.isPreferred()
        );
    }

    default UUID parentId(ProductCategory category) {
        return category.getParent() == null ? null : category.getParent().getId();
    }
}
