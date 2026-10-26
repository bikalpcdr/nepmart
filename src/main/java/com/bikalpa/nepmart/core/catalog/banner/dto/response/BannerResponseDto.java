package com.bikalpa.nepmart.core.catalog.banner.dto.response;

import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerResponseDto extends BaseDto {
    private String title;
    private String subtitle;
    private String imageUrl;
    private String targetUrl;
    private BannerPlacement placement;
    private Integer displayOrder;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
