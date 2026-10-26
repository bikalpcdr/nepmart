package com.bikalpa.nepmart.core.order.dto.response;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class OrderItemResponseDto extends BaseDto {
    private String productName;
    private String vendorName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
}
