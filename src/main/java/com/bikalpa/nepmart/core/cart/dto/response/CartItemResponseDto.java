package com.bikalpa.nepmart.core.cart.dto.response;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CartItemResponseDto extends BaseDto {
    private UUID productUuid;
    private String productName;
    private String productSlug;
    private String imageUrl;
    private String vendorName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
}
