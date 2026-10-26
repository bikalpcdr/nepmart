package com.bikalpa.nepmart.core.catalog.banner.service;

import com.bikalpa.nepmart.core.catalog.banner.dto.request.BannerRequestDto;
import com.bikalpa.nepmart.core.catalog.banner.dto.response.BannerResponseDto;
import com.bikalpa.nepmart.core.catalog.banner.entity.Banner;
import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;

import java.util.List;
import java.util.UUID;

public interface BannerService {
    void createBanner(BannerRequestDto request);
    BannerResponseDto getBanner(UUID uuid);
    BasePaginationResponseDto<BannerResponseDto> getBanners(BannerPlacement placement, BasePaginationDto page);
    List<BannerResponseDto> getLiveBanners(BannerPlacement placement);
    void updateBanner(UUID uuid, BannerRequestDto request);
    void deleteBanner(UUID uuid);
    Banner getEntity(UUID uuid);
}
