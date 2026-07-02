package com.nexware.product.service;

import com.nexware.product.dto.ProductCategoryCreateRequest;
import com.nexware.product.dto.ProductCategoryResponse;
import com.nexware.product.dto.ProductCreateRequest;
import com.nexware.product.dto.ProductResponse;
import com.nexware.product.dto.ProductSearchCriteria;
import com.nexware.product.dto.ProductSerialNumberResponse;
import com.nexware.product.dto.ProductUpdateRequest;
import com.nexware.product.dto.SerialNumberCreateRequest;
import com.nexware.product.dto.SupplierSummaryResponse;
import com.nexware.product.entity.Product;
import com.nexware.product.entity.ProductCategory;
import com.nexware.product.entity.ProductSerialNumber;
import com.nexware.product.entity.ProductSupplier;
import com.nexware.product.event.ProductCreatedEvent;
import com.nexware.product.event.ProductDeletedEvent;
import com.nexware.product.event.ProductUpdatedEvent;
import com.nexware.product.mapper.ProductMapper;
import com.nexware.product.repository.ProductCategoryRepository;
import com.nexware.product.repository.ProductRepository;
import com.nexware.product.repository.ProductSerialNumberRepository;
import com.nexware.product.repository.ProductSpecifications;
import com.nexware.product.repository.ProductSupplierRepository;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ConflictException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.supplier.entity.Supplier;
import com.nexware.supplier.repository.SupplierRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductSerialNumberRepository serialNumberRepository;
    private final ProductSupplierRepository productSupplierRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public ProductCategoryResponse createCategory(ProductCategoryCreateRequest request) {
        String code = normalizeCode(request.code());
        if (categoryRepository.existsByCode(code)) {
            throw new ConflictException("Product category code already exists");
        }

        ProductCategory parent = resolveParentCategory(request.parentId());
        ProductCategory category = ProductCategory.create(
                code,
                request.name().trim(),
                trimToNull(request.description()),
                parent
        );

        return productMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> listCategories() {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(productMapper::toCategoryResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String sku = normalizeSku(request.sku());
        validateSkuAvailable(sku, null);

        String barcode = trimToNull(request.barcode());
        validateBarcodeAvailable(barcode, null);

        ProductCategory category = requireActiveCategory(request.categoryId());
        Product product = Product.create(
                category,
                sku,
                barcode,
                request.name().trim(),
                trimToNull(request.description()),
                trimToNull(request.brand()),
                trimToNull(request.model()),
                request.unitCost(),
                request.unitPrice(),
                request.lowStockThreshold(),
                request.trackSerialNumbers()
        );

        Product savedProduct = productRepository.save(product);
        replaceSupplierMappings(savedProduct, request.supplierIds());
        eventPublisher.publishEvent(new ProductCreatedEvent(savedProduct.getId(), savedProduct.getSku(), Instant.now(clock)));
        return toProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        return toProductResponse(requireProduct(productId));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        return productRepository.findAll(ProductSpecifications.matching(criteria), pageable)
                .map(this::toProductResponse);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product = requireProduct(productId);
        String barcode = trimToNull(request.barcode());
        validateBarcodeAvailable(barcode, productId);

        ProductCategory category = requireActiveCategory(request.categoryId());
        product.update(
                category,
                barcode,
                request.name().trim(),
                trimToNull(request.description()),
                trimToNull(request.brand()),
                trimToNull(request.model()),
                request.unitCost(),
                request.unitPrice(),
                request.lowStockThreshold(),
                request.trackSerialNumbers(),
                request.active()
        );

        replaceSupplierMappings(product, request.supplierIds());
        eventPublisher.publishEvent(new ProductUpdatedEvent(product.getId(), product.getSku(), Instant.now(clock)));
        return toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = requireProduct(productId);
        product.softDelete(Instant.now(clock));
        eventPublisher.publishEvent(new ProductDeletedEvent(product.getId(), product.getSku(), Instant.now(clock)));
    }

    @Transactional
    public ProductSerialNumberResponse addSerialNumber(UUID productId, SerialNumberCreateRequest request) {
        Product product = requireProduct(productId);
        if (!product.isTrackSerialNumbers()) {
            throw new BusinessRuleViolationException("Product is not configured for serial number tracking");
        }

        String serialNumber = request.serialNumber().trim();
        if (serialNumberRepository.existsBySerialNumber(serialNumber)) {
            throw new ConflictException("Serial number already exists");
        }

        ProductSerialNumber savedSerialNumber = serialNumberRepository.save(
                ProductSerialNumber.create(product, serialNumber, request.warehouseId())
        );
        return productMapper.toSerialNumberResponse(savedSerialNumber);
    }

    @Transactional(readOnly = true)
    public List<ProductSerialNumberResponse> listSerialNumbers(UUID productId) {
        requireProduct(productId);
        return serialNumberRepository.findAllByProductIdOrderBySerialNumberAsc(productId).stream()
                .map(productMapper::toSerialNumberResponse)
                .toList();
    }

    private ProductResponse toProductResponse(Product product) {
        List<SupplierSummaryResponse> suppliers = productSupplierRepository.findAllByProductId(product.getId()).stream()
                .map(productMapper::toSupplierSummary)
                .toList();
        return productMapper.toProductResponse(product, suppliers);
    }

    private void replaceSupplierMappings(Product product, Set<UUID> supplierIds) {
        productSupplierRepository.deleteAllByProductId(product.getId());
        Set<UUID> uniqueSupplierIds = supplierIds == null ? Set.of() : new LinkedHashSet<>(supplierIds);
        if (uniqueSupplierIds.isEmpty()) {
            return;
        }

        List<Supplier> suppliers = supplierRepository.findAllByIdInAndDeletedAtIsNull(List.copyOf(uniqueSupplierIds));
        if (suppliers.size() != uniqueSupplierIds.size()) {
            throw new ResourceNotFoundException("One or more suppliers were not found");
        }

        List<ProductSupplier> mappings = suppliers.stream()
                .map(supplier -> ProductSupplier.create(product, supplier, false))
                .toList();
        productSupplierRepository.saveAll(mappings);
    }

    private Product requireProduct(UUID productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    private ProductCategory requireActiveCategory(UUID categoryId) {
        return categoryRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category", categoryId));
    }

    private ProductCategory resolveParentCategory(UUID parentId) {
        if (parentId == null) {
            return null;
        }
        return requireActiveCategory(parentId);
    }

    private void validateSkuAvailable(String sku, UUID currentProductId) {
        productRepository.findBySkuAndDeletedAtIsNull(sku)
                .filter(existing -> !existing.getId().equals(currentProductId))
                .ifPresent(existing -> {
                    throw new ConflictException("SKU already exists");
                });
    }

    private void validateBarcodeAvailable(String barcode, UUID currentProductId) {
        if (!StringUtils.hasText(barcode)) {
            return;
        }
        productRepository.findByBarcodeAndDeletedAtIsNull(barcode)
                .filter(existing -> !existing.getId().equals(currentProductId))
                .ifPresent(existing -> {
                    throw new ConflictException("Barcode already exists");
                });
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
