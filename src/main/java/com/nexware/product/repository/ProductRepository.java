package com.nexware.product.repository;

import com.nexware.product.entity.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Product> findBySkuAndDeletedAtIsNull(String sku);

    Optional<Product> findByBarcodeAndDeletedAtIsNull(String barcode);
}
