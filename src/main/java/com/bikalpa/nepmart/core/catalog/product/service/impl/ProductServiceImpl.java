package com.bikalpa.nepmart.core.catalog.product.service.impl;

import com.bikalpa.nepmart.common.security.VendorAccess;
import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import com.bikalpa.nepmart.core.catalog.category.service.CategoryService;
import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductSearchRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.response.ProductResponseDto;
import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.core.catalog.product.repo.ProductRepo;
import com.bikalpa.nepmart.core.catalog.product.service.ProductService;
import com.bikalpa.nepmart.core.catalog.product.utility.ProductConverter;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.service.VendorService;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.PageConstant.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final VendorService vendorService;
    private final CategoryService categoryService;
    private final VendorAccess vendorAccess;

    @Override
    public void createProduct(ProductRequestDto request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new GenericUncheckedException("Product name is required", "PRODUCT_NAME_REQUIRED", BAD_REQUEST);
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new GenericUncheckedException("Product price must be zero or greater", "PRODUCT_PRICE_INVALID", BAD_REQUEST);
        }
        if (request.getCompareAtPrice() != null && request.getCompareAtPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new GenericUncheckedException("Compare-at price must be zero or greater", "PRODUCT_PRICE_INVALID", BAD_REQUEST);
        }
        if (request.getStockQuantity() != null && request.getStockQuantity() < 0) {
            throw new GenericUncheckedException("Stock quantity cannot be negative", "PRODUCT_STOCK_INVALID", BAD_REQUEST);
        }
        Vendor vendor = resolveVendor(request.getVendorUuid());
        vendorAccess.requireVendorAccess(vendor);
        String slug = resolveSlug(request.getSlug(), request.getName());
        if (productRepo.existsBySlug(slug)) {
            throw new GenericUncheckedException("Product slug already exists", "PRODUCT_SLUG_ALREADY_EXISTS", CONFLICT);
        }
        if (request.getSku() != null && !request.getSku().isBlank()
                && productRepo.existsByVendorIdAndSku(vendor.getId(), request.getSku().trim())) {
            throw new GenericUncheckedException("SKU already exists for this vendor", "PRODUCT_SKU_ALREADY_EXISTS", CONFLICT);
        }
        Product product = new Product();
        product.setVendor(vendor);
        product.setName(request.getName().trim());
        product.setSlug(slug);
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku() == null ? null : request.getSku().trim());
        product.setPrice(request.getPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT);
        product.setFeatured(Boolean.TRUE.equals(request.getFeatured()));
        product.setIsActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);
        if (request.getCategoryUuid() != null) {
            Category category = categoryService.getEntity(request.getCategoryUuid());
            product.setCategory(category);
        }
        productRepo.save(product);
        log.info("Product created with slug {}", slug);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(UUID uuid) {
        return ProductConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductBySlug(String slug) {
        Product product = productRepo.findBySlug(slug)
                .orElseThrow(() -> new GenericUncheckedException(
                        "Product not found with slug: " + slug, "PRODUCT_NOT_FOUND", NOT_FOUND));
        return ProductConverter.toResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<ProductResponseDto> searchProducts(ProductSearchRequestDto request) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new GenericUncheckedException("minPrice cannot exceed maxPrice", "PRODUCT_PRICE_RANGE_INVALID", BAD_REQUEST);
        }
        Page<ProductResponseDto> result = productRepo.searchProducts(
                empty(request.getSearch()),
                empty(request.getCategorySlug()),
                empty(request.getVendorSlug()),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getStatus(),
                request.getFeatured(),
                sortPageable(request)
        ).map(ProductConverter::toResponseDto);
        return PaginationMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getFeaturedProducts(int limit) {
        List<Product> featured = productRepo
                .findTop12ByIsActiveTrueAndStatusAndFeaturedTrueOrderByUpdatedAtDesc(ProductStatus.PUBLISHED);
        if (limit > 0 && featured.size() > limit) {
            featured = featured.subList(0, limit);
        }
        return featured.stream().map(ProductConverter::toResponseDto).toList();
    }

    @Override
    public void updateProduct(UUID uuid, ProductRequestDto request) {
        Product product = findByUuidOrThrow(uuid);
        vendorAccess.requireVendorAccess(product.getVendor());
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        String slug = resolveSlug(request.getSlug(), product.getName());
        if (productRepo.existsBySlugAndIdNot(slug, product.getId())) {
            throw new GenericUncheckedException("Product slug already exists", "PRODUCT_SLUG_ALREADY_EXISTS", CONFLICT);
        }
        product.setSlug(slug);
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericUncheckedException("Product price must be zero or greater", "PRODUCT_PRICE_INVALID", BAD_REQUEST);
            }
            product.setPrice(request.getPrice());
        }
        if (request.getCompareAtPrice() != null) product.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getStockQuantity() != null) {
            if (request.getStockQuantity() < 0) {
                throw new GenericUncheckedException("Stock quantity cannot be negative", "PRODUCT_STOCK_INVALID", BAD_REQUEST);
            }
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getSku() != null) {
            String sku = request.getSku().trim();
            if (!sku.isBlank() && !sku.equals(product.getSku())
                    && productRepo.existsByVendorIdAndSkuAndIdNot(product.getVendor().getId(), sku, product.getId())) {
                throw new GenericUncheckedException("SKU already exists for this vendor", "PRODUCT_SKU_ALREADY_EXISTS", CONFLICT);
            }
            product.setSku(sku.isBlank() ? null : sku);
        }
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getFeatured() != null) product.setFeatured(request.getFeatured());
        if (request.getActive() != null) product.setIsActive(request.getActive());
        if (request.getCategoryUuid() != null) {
            product.setCategory(categoryService.getEntity(request.getCategoryUuid()));
        }
        productRepo.save(product);
        log.info("Product updated with uuid {}", uuid);
    }

    @Override
    public void deleteProduct(UUID uuid) {
        Product product = findByUuidOrThrow(uuid);
        vendorAccess.requireVendorAccess(product.getVendor());
        product.setIsActive(Boolean.FALSE);
        product.setStatus(ProductStatus.ARCHIVED);
        productRepo.save(product);
        log.info("Product archived with uuid {}", uuid);
    }

    @Override
    public void publishProduct(UUID uuid) {
        Product product = findByUuidOrThrow(uuid);
        vendorAccess.requireVendorAccess(product.getVendor());
        product.setStatus(ProductStatus.PUBLISHED);
        productRepo.save(product);
    }

    @Override
    public void archiveProduct(UUID uuid) {
        Product product = findByUuidOrThrow(uuid);
        vendorAccess.requireVendorAccess(product.getVendor());
        product.setStatus(ProductStatus.ARCHIVED);
        productRepo.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPublished() {
        return productRepo.countByIsActiveTrueAndStatus(ProductStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    private Vendor resolveVendor(UUID vendorUuid) {
        if (vendorUuid != null) {
            return vendorService.getEntity(vendorUuid);
        }
        User user = vendorAccess.currentUser();
        return vendorAccess.firstVendorOf(user)
                .orElseThrow(() -> new GenericUncheckedException(
                        "A vendor is required to create a product", "PRODUCT_VENDOR_REQUIRED", BAD_REQUEST));
    }

    private Product findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(productRepo, uuid, "Product not found with uuid: " + uuid, "PRODUCT_NOT_FOUND");
    }

    private String resolveSlug(String slug, String name) {
        String resolved = toSlug((slug == null || slug.isBlank()) ? name : slug);
        if (resolved.isBlank()) {
            throw new GenericUncheckedException("Product slug is required", "PRODUCT_SLUG_REQUIRED", BAD_REQUEST);
        }
        return resolved;
    }

    private String toSlug(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
    }

    private String empty(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    private Pageable sortPageable(ProductSearchRequestDto request) {
        int page = request.getPage() == null ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null ? DEFAULT_SIZE : request.getSize();
        if (page < DEFAULT_PAGE) {
            throw new GenericUncheckedException(MessageConstant.INVALID_PAGE_MESSAGE, MessageConstant.INVALID_PAGE, BAD_REQUEST);
        }
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new GenericUncheckedException(MessageConstant.INVALID_PAGE_SIZE_MESSAGE, MessageConstant.INVALID_PAGE_SIZE, BAD_REQUEST);
        }
        String sort = request.getSort() == null ? "newest" : request.getSort().toLowerCase(Locale.ROOT);
        Sort springSort = switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return PageRequest.of(page, size, springSort);
    }
}
