package com.bikalpa.nepmart.core.notification.utility;

import com.bikalpa.nepmart.core.notification.entity.PushSubscription;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.infra.config.WebPushConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Urgency;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

/**
 * Wraps the {@code web-push} library so every caller sends a push in one
 * method. VAPID is configured once via Spring properties and shared by all
 * senders, so a single broken endpoint never takes down the rest.
 */
@Component
@Slf4j
public class WebPushSender {

    private static final int TTL_SECONDS = 60;

    private final PushService pushService;
    private final ObjectMapper objectMapper;

    public WebPushSender(WebPushConfig config, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        pushService = new PushService();
        WebPushConfig.Vapid vapid = config.getVapid();
        try {
            pushService.setSubject(vapid.getSubject());
            pushService.setPublicKey(vapid.getPublicKey());
            pushService.setPrivateKey(vapid.getPrivateKey());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            log.warn("Failed to configure Web Push VAPID keys - push delivery disabled", e);
        }
        if (vapid.getSubject() == null || vapid.getPublicKey() == null
                || vapid.getPrivateKey() == null) {
            log.warn("Web Push VAPID keys are not configured -- push delivery is disabled");
        }
    }

    /**
     * Sends one push to a single browser subscription. Throws on any HTTP/JOSE
     * failure so the caller can log and continue with the remaining
     * subscriptions. A push failure is not a business failure: the outbox
     * keeps the event and retries on the next scheduled run.
     *
     * @throws Exception when the push could not be sent (HTTP 4xx/5xx, network,
     *                   crypto failure). The caller decides whether to retry
     *                   or skip.
     */
    public void send(User user, PushSubscription subscription, UUID notificationId,
                     String title, String body, String data) throws Exception {
        String payload = objectMapper.writeValueAsString(payload(user, notificationId, title, body, data));
        nl.martijndwars.webpush.Notification notification =
                nl.martijndwars.webpush.Notification.builder()
                        .endpoint(subscription.getEndpoint())
                        .userPublicKey(subscription.getP256dh())
                        .userAuth(subscription.getAuth())
                        .payload(payload)
                        .ttl(TTL_SECONDS)
                        .urgency(Urgency.HIGH)
                        .build();
        pushService.send(notification);
        log.debug("Push delivered to {} for user {}", subscription.getEndpoint(), user.getId());
    }

    private PushPayload payload(User user, UUID notificationId, String title,
                                String body, String data) {
        return new PushPayload(notificationId, title, body, data, user.getEmail());
    }

    /**
     * Narrow JSON contract forwarded to the browser via Web Push.
     * notificationId links the push back to the persisted in-app notification;
     * recipient is the user's email (shown as the push sender where supported).
     */
    private record PushPayload(UUID notificationId, String title, String body,
                               String data, String recipient) {
    }
}
