package com.bikalpa.nepmart.core.catalog.banner.utility;

import com.bikalpa.nepmart.core.catalog.banner.dto.response.BannerResponseDto;
import com.bikalpa.nepmart.core.catalog.banner.entity.Banner;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@UtilityClass
public class BannerConverter {

    public static BannerResponseDto toResponseDto(Banner banner) {
        if (banner == null) {
            return null;
        }
        BannerResponseDto response = new BannerResponseDto();
        response.setUuid(banner.getUuid());
        response.setIsActive(banner.getIsActive());
        response.setTitle(banner.getTitle());
        response.setSubtitle(banner.getSubtitle());
        response.setImageUrl(banner.getImageUrl());
        response.setTargetUrl(banner.getTargetUrl());
        response.setPlacement(banner.getPlacement());
        response.setDisplayOrder(banner.getDisplayOrder());
        response.setStartsAt(banner.getStartsAt());
        response.setEndsAt(banner.getEndsAt());
        return response;
    }
}
