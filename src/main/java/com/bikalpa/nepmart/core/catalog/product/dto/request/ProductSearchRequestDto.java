package com.bikalpa.nepmart.core.catalog.product.dto.request;

import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
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
public class ProductSearchRequestDto extends BasePaginationDto {
    private String search;
    private String categorySlug;
    private String vendorSlug;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ProductStatus status;
    private Boolean featured;
    private String sort = "newest";
}
