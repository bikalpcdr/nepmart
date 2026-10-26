package com.bikalpa.nepmart.core.catalog.category.entity;

import com.bikalpa.nepmart.core.catalog.category.enums.CategoryStatus;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Platform category tree supporting nesting via parent.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categories_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_categories_parent", columnList = "parent_id"),
                @Index(name = "idx_categories_status", columnList = "status")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CategoryStatus status = CategoryStatus.ACTIVE;
}
