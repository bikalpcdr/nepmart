package com.bikalpa.nepmart.core.catalog.product.dto.response;

import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDto extends BaseDto {
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer stockQuantity;
    private String imageUrl;
    private String categoryName;
    private String categorySlug;
    private String vendorName;
    private String vendorSlug;
    private ProductStatus status;
    private Boolean featured;
}
