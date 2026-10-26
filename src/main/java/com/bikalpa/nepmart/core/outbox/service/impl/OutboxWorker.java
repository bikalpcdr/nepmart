package com.bikalpa.nepmart.core.outbox.service.impl;

import com.bikalpa.nepmart.core.notification.service.NotificationService;
import com.bikalpa.nepmart.core.outbox.entity.OutboxEvent;
import com.bikalpa.nepmart.core.outbox.enums.OutboxStatus;
import com.bikalpa.nepmart.core.outbox.repo.OutboxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Claims due outbox events inside one transaction (FOR UPDATE SKIP LOCKED), so
 * every instance picks up a disjoint chunk. Failed events re-run after a
 * backoff; permanently rejected ones stay PENDING forever and need manual
 * intervention instead of silently looping.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxWorker {

    private static final int MAX_BATCH = 50;
    private static final long FIRST_BACKOFF_MS = 60_000L;
    private static final long MAX_BACKOFF_MS = 3_600_000L;

    private final OutboxRepo outboxRepo;
    private final NotificationService notificationService;

    @Scheduled(fixedDelayString = "#{'10000'}")
    @Transactional
    public void processBatch() {
        List<OutboxEvent> events = outboxRepo.claimDue(LocalDateTime.now(), MAX_BATCH);
        for (OutboxEvent event : events) {
            try {
                send(event);
            } catch (Exception ex) {
                ack(event, -1, ex.getMessage());
            }
        }
        if (!events.isEmpty()) {
            log.info("Processed {} outbox event(s)", events.size());
        }
    }

    private void send(OutboxEvent event) {
        switch (event.getEventType()) {
            case "PUSH_DELIVERY" -> {
                // JSON payload here is the only part that ever reaches the wire.
                // Title/body must be read from JSON in the controller/service layer.
                notificationService.sendPush(event.getPayload());
            }
            default -> log.warn("Unhandled outbox event type: {}", event.getEventType());
        }
        ack(event, 0, null);
    }

    private void ack(OutboxEvent event, int result, String error) {
        event.setProcessedAt(LocalDateTime.now());
        event.setStatus(result == 0 ? OutboxStatus.DONE : OutboxStatus.FAILED);
        if (error != null) {
            event.setLastError(error);
            int next = event.getRetryCount() == null ? 0 : event.getRetryCount();
            long backoff = Math.min(FIRST_BACKOFF_MS * (1L << Math.min(next, 12)),
                    MAX_BACKOFF_MS);
            event.setRetryCount(next + 1);
            event.setNextAttemptAt(LocalDateTime.now().plusSeconds(backoff / 1000));
        } else {
            event.setRetryCount(0);
            event.setLastError(null);
        }
        outboxRepo.save(event);
    }
}
