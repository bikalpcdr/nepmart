package com.bikalpa.nepmart.core.catalog.product.entity;

import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Vendor-owned product. Public catalog exposes PUBLISHED + active rows only.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_products_vendor_sku", columnNames = {"vendor_id", "sku"}),
                @UniqueConstraint(name = "uk_products_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_products_vendor", columnList = "vendor_id"),
                @Index(name = "idx_products_category", columnList = "category_id"),
                @Index(name = "idx_products_status", columnList = "status"),
                @Index(name = "idx_products_featured", columnList = "featured")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, length = 220)
    private String slug;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "compare_at_price", precision = 19, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_public_id", length = 300)
    private String imagePublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "featured", nullable = false)
    private Boolean featured = Boolean.FALSE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}
