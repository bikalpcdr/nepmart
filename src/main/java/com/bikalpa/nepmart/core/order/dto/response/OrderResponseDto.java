package com.bikalpa.nepmart.core.order.dto.response;

import com.bikalpa.nepmart.core.order.enums.OrderStatus;
import com.bikalpa.nepmart.core.order.enums.PaymentStatus;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class OrderResponseDto extends BaseDto {
    private String orderNumber;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal total;
    private String shippingAddress;
    private String contactPhone;
    private List<OrderItemResponseDto> items;
}
