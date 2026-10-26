package com.bikalpa.nepmart.core.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CreateOrderRequestDto {
    @NotBlank
    private String shippingAddress;
    private String contactPhone;
}
