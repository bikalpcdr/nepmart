package com.bikalpa.nepmart.core.catalog.product.dto.request;

import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    @Size(max = 200)
    private String name;

    @Size(max = 220)
    private String slug;

    @Size(max = 500)
    private String shortDescription;

    private String description;

    @Size(max = 100)
    private String sku;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal compareAtPrice;

    @Min(0)
    private Integer stockQuantity;

    @Size(max = 500)
    private String imageUrl;

    private UUID categoryUuid;

    private UUID vendorUuid;

    private ProductStatus status;

    private Boolean featured;

    private Boolean active;
}
