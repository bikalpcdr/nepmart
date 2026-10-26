package com.bikalpa.nepmart.core.catalog.product.service;

import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductSearchRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.response.ProductResponseDto;
import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    void createProduct(ProductRequestDto request);
    ProductResponseDto getProduct(UUID uuid);
    ProductResponseDto getProductBySlug(String slug);
    BasePaginationResponseDto<ProductResponseDto> searchProducts(ProductSearchRequestDto request);
    List<ProductResponseDto> getFeaturedProducts(int limit);
    void updateProduct(UUID uuid, ProductRequestDto request);
    long countPublished();
    void deleteProduct(UUID uuid);
    void publishProduct(UUID uuid);
    void archiveProduct(UUID uuid);
    Product getEntity(UUID uuid);
}
