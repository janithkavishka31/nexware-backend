package com.nexware.product.repository;

import com.nexware.product.entity.ProductSupplier;
import com.nexware.product.entity.ProductSupplierId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, ProductSupplierId> {

    @EntityGraph(attributePaths = "supplier")
    @Query("select mapping from ProductSupplier mapping where mapping.product.id = :productId")
    List<ProductSupplier> findAllByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("delete from ProductSupplier mapping where mapping.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}
