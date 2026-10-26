package com.bikalpa.nepmart.core.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PushSubscriptionRequestDto {
    @NotBlank
    private String endpoint;
    @NotBlank
    private String p256dh;
    @NotBlank
    private String auth;
    private String userAgent;
}
