package com.bikalpa.nepmart.core.notification.dto.response;

import com.bikalpa.nepmart.core.notification.enums.NotificationType;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class NotificationResponseDto extends BaseDto {
    private String title;
    private String message;
    private NotificationType type;
    private String targetUrl;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
