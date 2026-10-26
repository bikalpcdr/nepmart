package com.bikalpa.nepmart.core.outbox.service.impl;

import com.bikalpa.nepmart.core.outbox.entity.OutboxEvent;
import com.bikalpa.nepmart.core.outbox.enums.OutboxStatus;
import com.bikalpa.nepmart.core.outbox.repo.OutboxRepo;
import com.bikalpa.nepmart.core.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepo outboxRepo;

    /**
     * Joins the caller transaction (REQUIRED default): the event row commits
     * atomically with the business change, or rolls back with it.
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String eventType, String aggregateType, UUID aggregateId, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setEventType(eventType);
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setPayload(payload);
        event.setStatus(OutboxStatus.PENDING);
        event.setRetryCount(0);
        event.setNextAttemptAt(LocalDateTime.now());
        outboxRepo.save(event);
    }
}
