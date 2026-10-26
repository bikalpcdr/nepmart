package com.bikalpa.nepmart.core.cart.dto.response;

import com.bikalpa.nepmart.core.cart.enums.CartStatus;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CartResponseDto extends BaseDto {
    private CartStatus status;
    private List<CartItemResponseDto> items;
    private BigDecimal subtotal;
    private int totalItems;
}
