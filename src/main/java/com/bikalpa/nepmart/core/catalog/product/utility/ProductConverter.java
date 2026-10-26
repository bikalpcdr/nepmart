package com.bikalpa.nepmart.core.catalog.product.utility;

import com.bikalpa.nepmart.core.catalog.product.dto.response.ProductResponseDto;
import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@UtilityClass
public class ProductConverter {

    public static ProductResponseDto toResponseDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDto response = new ProductResponseDto();
        response.setUuid(product.getUuid());
        response.setIsActive(product.getIsActive());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setShortDescription(product.getShortDescription());
        response.setDescription(product.getDescription());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setCompareAtPrice(product.getCompareAtPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());
        response.setFeatured(product.getFeatured());
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
            response.setCategorySlug(product.getCategory().getSlug());
        }
        if (product.getVendor() != null) {
            response.setVendorName(product.getVendor().getName());
            response.setVendorSlug(product.getVendor().getSlug());
        }
        return response;
    }
}
