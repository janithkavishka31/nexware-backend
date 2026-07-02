package com.nexware.product.repository;

import com.nexware.product.entity.ProductCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    Optional<ProductCategory> findByIdAndActiveTrue(UUID id);

    boolean existsByCode(String code);

    List<ProductCategory> findAllByActiveTrueOrderByNameAsc();
}
