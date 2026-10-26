package com.bikalpa.nepmart.core.outbox.repo;

import com.bikalpa.nepmart.core.outbox.entity.OutboxEvent;
import com.bikalpa.nepmart.core.outbox.enums.OutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxRepo extends JpaRepository<OutboxEvent, Long> {

    /**
     * Safe claiming for concurrent workers: SKIP LOCKED never blocks on a row
     * another instance already locked. Requires a surrounding transaction.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select * from outbox_events where status = 'PENDING' "
            + "and next_attempt_at <= :now order by created_at asc limit :limit for update skip locked",
            nativeQuery = true)
    List<OutboxEvent> claimDue(@Param("now") LocalDateTime now, @Param("limit") int limit);

    @Modifying
    @Query("delete from OutboxEvent e where e.status = 'DONE' and e.processedAt < :before")
    int deleteProcessedBefore(@Param("before") LocalDateTime before);
}
