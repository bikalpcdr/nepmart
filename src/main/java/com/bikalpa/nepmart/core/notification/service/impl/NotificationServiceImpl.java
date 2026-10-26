package com.bikalpa.nepmart.core.notification.service.impl;

import com.bikalpa.nepmart.common.security.VendorAccess;
import com.bikalpa.nepmart.core.notification.dto.request.PushSubscriptionRequestDto;
import com.bikalpa.nepmart.core.notification.dto.response.NotificationResponseDto;
import com.bikalpa.nepmart.core.notification.dto.response.PushSubscriptionResponseDto;
import com.bikalpa.nepmart.core.notification.entity.AppNotification;
import com.bikalpa.nepmart.core.notification.entity.PushSubscription;
import com.bikalpa.nepmart.core.notification.enums.NotificationType;
import com.bikalpa.nepmart.core.notification.repo.NotificationRepo;
import com.bikalpa.nepmart.core.notification.repo.PushSubscriptionRepo;
import com.bikalpa.nepmart.core.notification.service.NotificationService;
import com.bikalpa.nepmart.core.notification.utility.WebPushSender;
import com.bikalpa.nepmart.core.outbox.service.OutboxService;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final PushSubscriptionRepo subscriptionRepo;
    private final VendorAccess vendorAccess;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final WebPushSender webPushSender;

    @Override
    public AppNotification create(User recipient, Vendor vendor, String title, String message,
                                  NotificationType type, String targetUrl) {
        try {
            AppNotification notification = new AppNotification();
            notification.setRecipient(recipient);
            notification.setVendor(vendor);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type == null ? NotificationType.GENERAL : type);
            notification.setTargetUrl(targetUrl);
            notification.setIsActive(Boolean.TRUE);
            notification = notificationRepo.save(notification);
            outboxService.enqueue("PUSH_DELIVERY", "notification", notification.getUuid(),
                    toPayload(notification.getUuid(), title, message, targetUrl));
            return notification;
        } catch (Exception ex) {
            log.error("Failed to create notification [{}] for user {}", type, recipient.getId(), ex.getMessage());
            throw ex;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<NotificationResponseDto> getMyNotifications(BasePaginationDto page) {
        User user = vendorAccess.currentUser();
        return PaginationMapper.toResponse(notificationRepo
                .findByRecipientIdOrderByCreatedAtDesc(user.getId(),
                        PageRequest.of(page.getPage(), page.getSize(), Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User user = vendorAccess.currentUser();
        return notificationRepo.countByRecipientIdAndReadAtIsNullAndIsActiveTrue(user.getId());
    }

    @Override
    public void markAsRead(UUID uuid) {
        User user = vendorAccess.currentUser();
        AppNotification notification = EntityLookup.findByUuid(notificationRepo, uuid,
                "Notification not found with uuid: " + uuid, "NOTIFICATION_NOT_FOUND");
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new GenericUncheckedException("You cannot update another user notification",
                    "NOTIFICATION_FORBIDDEN", FORBIDDEN);
        }
        notification.setReadAt(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    @Override
    public void markAllAsRead() {
        User user = vendorAccess.currentUser();
        notificationRepo.markAllAsRead(user.getId());
    }

    @Override
    public PushSubscriptionResponseDto saveSubscription(PushSubscriptionRequestDto request) {
        User user = vendorAccess.currentUser();
        PushSubscription subscription = subscriptionRepo
                .findByUserIdAndEndpoint(user.getId(), request.getEndpoint())
                .orElseGet(() -> {
                    PushSubscription created = new PushSubscription();
                    created.setUser(user);
                    created.setEndpoint(request.getEndpoint());
                    created.setIsActive(Boolean.TRUE);
                    return created;
                });
        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new GenericUncheckedException("Subscription belongs to another user",
                    "SUBSCRIPTION_FORBIDDEN", FORBIDDEN);
        }
        subscription.setP256dh(request.getP256dh());
        subscription.setAuth(request.getAuth());
        subscription.setUserAgent(request.getUserAgent());
        subscription.setIsActive(Boolean.TRUE);
        subscription.setLastUsedAt(LocalDateTime.now());
        subscription = subscriptionRepo.save(subscription);
        return toDto(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PushSubscriptionResponseDto> getMySubscriptions() {
        User user = vendorAccess.currentUser();
        return subscriptionRepo.findByUserIdAndIsActiveTrue(user.getId()).stream().map(this::toDto).toList();
    }

    @Override
    public void deleteSubscription(UUID uuid) {
        User user = vendorAccess.currentUser();
        PushSubscription subscription = EntityLookup.findByUuid(subscriptionRepo, uuid,
                "Push subscription not found with uuid: " + uuid, "SUBSCRIPTION_NOT_FOUND");
        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new GenericUncheckedException("You cannot delete another user subscription",
                    "SUBSCRIPTION_FORBIDDEN", FORBIDDEN);
        }
        subscription.setIsActive(Boolean.FALSE);
        subscriptionRepo.save(subscription);
    }

    /**
     * Outbox entry point: payload is a Jackson-serialized object with keys
     * notificationId, title, body, data. Used by {@link com.bikalpa.nepmart.core.outbox.service.impl.OutboxWorker}.
     */
    @Override
    public void sendPush(String payload) {
        try {
            OutboxPayload po = objectMapper.readValue(payload, OutboxPayload.class);
            deliverAll(po.notificationId(), po.title(), po.body(), po.data());
        } catch (Exception ex) {
            log.error("Failed to dispatch outbox push payload: {}", payload, ex.getMessage());
        }
    }

    /**
     * Delivers a push to every active browser subscription of the current user.
     * One dead endpoint must never abort delivery to the others.
     */
    void deliverAll(UUID notificationId, String title, String body, String data) {
        try {
            User user = vendorAccess.currentUser();
            List<PushSubscription> subscriptions =
                    subscriptionRepo.findByUserIdAndIsActiveTrue(user.getId());
            for (PushSubscription subscription : subscriptions) {
                try {
                    webPushSender.send(user, subscription, notificationId, title, body, data);
                } catch (Exception ex) {
                    log.warn("Push delivery failed for subscription {} of user {}",
                            subscription.getEndpoint(), user.getId(), ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error("Failed to deliver push for notification {}", notificationId, ex.getMessage());
        }
    }

    private NotificationResponseDto toDto(AppNotification notification) {
        NotificationResponseDto dto = NotificationResponseDto.builder()
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .targetUrl(notification.getTargetUrl())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
        dto.setUuid(notification.getUuid());
        dto.setIsActive(notification.getIsActive());
        return dto;
    }

    private PushSubscriptionResponseDto toDto(PushSubscription subscription) {
        PushSubscriptionResponseDto dto = PushSubscriptionResponseDto.builder()
                .endpoint(subscription.getEndpoint())
                .userAgent(subscription.getUserAgent())
                .build();
        dto.setUuid(subscription.getUuid());
        dto.setIsActive(subscription.getIsActive());
        return dto;
    }

    private String toPayload(UUID notificationId, String title, String body, String data) {
        try {
            return objectMapper.writeValueAsString(new OutboxPayload(notificationId, title, body, data));
        } catch (Exception ex) {
            throw new GenericUncheckedException("Failed to serialize push payload", "PUSH_SERIALIZE_FAILED", INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Narrow JSON contract shared by the application and the outbox worker.
     * title/body/data are the user-facing push fields; notificationId links
     * back to the persisted in-app notification.
     */
    private record OutboxPayload(UUID notificationId, String title, String body, String data) {
    }
}
