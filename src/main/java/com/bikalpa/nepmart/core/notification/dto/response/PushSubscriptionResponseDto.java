package com.bikalpa.nepmart.core.notification.dto.response;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PushSubscriptionResponseDto extends BaseDto {
    private String endpoint;
    private String userAgent;
}
