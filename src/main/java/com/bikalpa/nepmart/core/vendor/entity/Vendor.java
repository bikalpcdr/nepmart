package com.bikalpa.nepmart.core.vendor.entity;

import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Entity
@Table(
        name = "vendors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vendors_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vendor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private VendorStatus status = VendorStatus.PENDING;
}
