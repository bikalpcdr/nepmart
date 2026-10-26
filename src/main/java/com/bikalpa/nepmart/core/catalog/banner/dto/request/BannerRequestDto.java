package com.bikalpa.nepmart.core.catalog.banner.dto.request;

import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class BannerRequestDto {

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 300)
    private String subtitle;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String targetUrl;

    private BannerPlacement placement;

    private Integer displayOrder;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private Boolean active;
}
