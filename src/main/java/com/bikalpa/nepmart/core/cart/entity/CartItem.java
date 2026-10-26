package com.bikalpa.nepmart.core.cart.entity;

import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_items_cart_product", columnNames = {"cart_id", "product_id"}),
        indexes = @Index(name = "idx_cart_items_cart", columnList = "cart_id"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
