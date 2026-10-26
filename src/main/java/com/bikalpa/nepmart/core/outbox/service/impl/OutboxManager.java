package com.bikalpa.nepmart.core.outbox.service.impl;

import com.bikalpa.nepmart.core.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public-facing facade over {@link OutboxServiceImpl}: callers inject this and
 * never touch the outbox table directly.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxManager implements OutboxService {

    private final OutboxServiceImpl impl;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String eventType, String aggregateType, java.util.UUID aggregateId, String payload) {
        impl.enqueue(eventType, aggregateType, aggregateId, payload);
        log.debug("Outbox event queued: type={}, aggregate={}, id={}", eventType, aggregateType, aggregateId);
    }
}
