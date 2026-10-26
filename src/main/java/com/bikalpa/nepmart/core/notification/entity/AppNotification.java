package com.bikalpa.nepmart.core.notification.entity;

import com.bikalpa.nepmart.core.notification.enums.NotificationType;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_recipient", columnList = "recipient_id"),
        @Index(name = "idx_notifications_vendor", columnList = "vendor_id")
})
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AppNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type = NotificationType.GENERAL;

    @Column(name = "target_url", length = 500)
    private String targetUrl;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
