package com.bikalpa.nepmart.core.notification.entity;

import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Browser Web Push subscription. Endpoint + keys are secrets: never logged,
 * never returned in list payloads beyond what the owner needs.
 */
@Entity
@Table(name = "push_subscriptions", indexes = {
        @Index(name = "idx_push_subs_user", columnList = "user_id"),
        @Index(name = "idx_push_subs_active", columnList = "active")
})
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PushSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "endpoint", nullable = false, length = 1000)
    private String endpoint;

    @Column(name = "p256dh", nullable = false, length = 500)
    private String p256dh;

    @Column(name = "auth", nullable = false, length = 500)
    private String auth;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
