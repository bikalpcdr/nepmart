package com.bikalpa.nepmart.core.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class UpdateCartItemRequestDto {
    @NotNull @Min(1)
    private Integer quantity;
}
