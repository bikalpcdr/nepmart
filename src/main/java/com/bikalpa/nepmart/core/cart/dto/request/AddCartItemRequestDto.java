package com.bikalpa.nepmart.core.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AddCartItemRequestDto {
    @NotNull
    private UUID productUuid;
    @Min(1)
    private Integer quantity = 1;
}
