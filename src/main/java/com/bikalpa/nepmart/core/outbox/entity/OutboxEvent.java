package com.bikalpa.nepmart.core.outbox.entity;

import com.bikalpa.nepmart.core.outbox.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transactional outbox: written in the same DB transaction as the business
 * change, claimed by a scheduled worker with FOR UPDATE SKIP LOCKED.
 */
@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_status_next", columnList = "status,next_attempt_at"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type,aggregate_id")
})
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
}
