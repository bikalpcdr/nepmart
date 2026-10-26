package com.bikalpa.nepmart.core.order.entity;

import com.bikalpa.nepmart.core.order.enums.OrderStatus;
import com.bikalpa.nepmart.core.order.enums.PaymentStatus;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer-facing order header. Totals are server-calculated; per-vendor
 * fulfillment is tracked through {@link OrderItem#vendor}.
 */
@Entity
@Table(name = "customer_orders", indexes = {
        @Index(name = "idx_orders_user", columnList = "user_id"),
        @Index(name = "idx_orders_status", columnList = "status")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
