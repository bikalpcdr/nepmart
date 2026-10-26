package com.bikalpa.nepmart.core.notification.service;

import com.bikalpa.nepmart.core.notification.dto.request.PushSubscriptionRequestDto;
import com.bikalpa.nepmart.core.notification.dto.response.NotificationResponseDto;
import com.bikalpa.nepmart.core.notification.dto.response.PushSubscriptionResponseDto;
import com.bikalpa.nepmart.core.notification.entity.AppNotification;
import com.bikalpa.nepmart.core.notification.enums.NotificationType;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    AppNotification create(User recipient, Vendor vendor, String title, String message,
                           NotificationType type, String targetUrl);

    /**
     * Outbox entry point: payload is a Jackson-serialized object with keys
     * notificationId, title, body, data. Used by {@link com.bikalpa.nepmart.core.outbox.service.impl.OutboxWorker}.
     */
    void sendPush(String payload);

    BasePaginationResponseDto<NotificationResponseDto> getMyNotifications(BasePaginationDto page);
    long getUnreadCount();
    void markAsRead(UUID uuid);
    void markAllAsRead();
    PushSubscriptionResponseDto saveSubscription(PushSubscriptionRequestDto request);
    List<PushSubscriptionResponseDto> getMySubscriptions();
    void deleteSubscription(UUID uuid);
}
