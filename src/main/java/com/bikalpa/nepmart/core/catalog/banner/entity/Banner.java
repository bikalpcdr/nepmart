package com.bikalpa.nepmart.core.catalog.banner.entity;

import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Promotional banner with placement + display window gating.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Entity
@Table(
        name = "banners",
        indexes = {
                @Index(name = "idx_banners_placement", columnList = "placement"),
                @Index(name = "idx_banners_active", columnList = "active")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "subtitle", length = 300)
    private String subtitle;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_public_id", length = 300)
    private String imagePublicId;

    @Column(name = "target_url", length = 500)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement", nullable = false, length = 30)
    private BannerPlacement placement = BannerPlacement.HOMEPAGE;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;
}
