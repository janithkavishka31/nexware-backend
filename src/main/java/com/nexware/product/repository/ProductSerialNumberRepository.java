package com.nexware.product.repository;

import com.nexware.product.entity.ProductSerialNumber;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSerialNumberRepository extends JpaRepository<ProductSerialNumber, UUID> {

    boolean existsBySerialNumber(String serialNumber);

    List<ProductSerialNumber> findAllByProductIdOrderBySerialNumberAsc(UUID productId);
}
