package com.bikalpa.nepmart.core.vendor.membership.entity;

import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.membership.enums.MembershipStatus;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_memberships",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vendor_memberships_user_vendor", columnNames = {"user_id", "vendor_id"})
        },
        indexes = {
                @Index(name = "idx_vendor_memberships_vendor", columnList = "vendor_id"),
                @Index(name = "idx_vendor_memberships_user", columnList = "user_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendorMembership extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MembershipStatus status = MembershipStatus.PENDING;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
