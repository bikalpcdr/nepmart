package com.bikalpa.nepmart.core.outbox.service;

import java.util.UUID;

public interface OutboxService {
    void enqueue(String eventType, String aggregateType, UUID aggregateId, String payload);
}
