package com.bikalpa.nepmart.core.notification.repo;

import com.bikalpa.nepmart.core.notification.entity.AppNotification;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends BaseRepository<AppNotification> {
    Page<AppNotification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId, Pageable pageable);

    long countByRecipientIdAndReadAtIsNullAndIsActiveTrue(Integer recipientId);

    @Modifying
    @Query("update AppNotification n set n.readAt = CURRENT_TIMESTAMP where n.recipient.id = :userId and n.readAt is null")
    int markAllAsRead(@Param("userId") Integer userId);
}
