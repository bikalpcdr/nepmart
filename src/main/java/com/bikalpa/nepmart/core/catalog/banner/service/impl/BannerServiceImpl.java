package com.bikalpa.nepmart.core.catalog.banner.service.impl;

import com.bikalpa.nepmart.core.catalog.banner.dto.request.BannerRequestDto;
import com.bikalpa.nepmart.core.catalog.banner.dto.response.BannerResponseDto;
import com.bikalpa.nepmart.core.catalog.banner.entity.Banner;
import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.core.catalog.banner.repo.BannerRepo;
import com.bikalpa.nepmart.core.catalog.banner.service.BannerService;
import com.bikalpa.nepmart.core.catalog.banner.utility.BannerConverter;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
public class BannerServiceImpl implements BannerService {

    private final BannerRepo bannerRepo;

    @Override
    public void createBanner(BannerRequestDto request) {
        Banner banner = new Banner();
        apply(banner, request);
        bannerRepo.save(banner);
        log.info("Banner created: {}", banner.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponseDto getBanner(UUID uuid) {
        return BannerConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<BannerResponseDto> getBanners(BannerPlacement placement, BasePaginationDto page) {
        Page<BannerResponseDto> result = bannerRepo.searchBanners(
                placement,
                PageRequest.of(page.getPage(), page.getSize(), Sort.by(Sort.Direction.DESC, "displayOrder"))
        ).map(BannerConverter::toResponseDto);
        return PaginationMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDto> getLiveBanners(BannerPlacement placement) {
        return bannerRepo.findLiveBanners(placement, LocalDateTime.now())
                .stream().map(BannerConverter::toResponseDto).toList();
    }

    @Override
    public void updateBanner(UUID uuid, BannerRequestDto request) {
        Banner banner = findByUuidOrThrow(uuid);
        apply(banner, request);
        bannerRepo.save(banner);
        log.info("Banner updated: {}", uuid);
    }

    @Override
    public void deleteBanner(UUID uuid) {
        Banner banner = findByUuidOrThrow(uuid);
        banner.setIsActive(Boolean.FALSE);
        bannerRepo.save(banner);
    }

    @Override
    @Transactional(readOnly = true)
    public Banner getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    private void apply(Banner banner, BannerRequestDto request) {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            banner.setTitle(request.getTitle().trim());
        } else if (banner.getId() == null) {
            throw new GenericUncheckedException("Banner title is required", "BANNER_TITLE_REQUIRED", BAD_REQUEST);
        }
        if (request.getSubtitle() != null) banner.setSubtitle(request.getSubtitle());
        if (request.getImageUrl() != null) banner.setImageUrl(request.getImageUrl());
        if (request.getTargetUrl() != null) banner.setTargetUrl(request.getTargetUrl());
        if (request.getPlacement() != null) banner.setPlacement(request.getPlacement());
        if (request.getDisplayOrder() != null) banner.setDisplayOrder(request.getDisplayOrder());
        if (request.getStartsAt() != null) banner.setStartsAt(request.getStartsAt());
        if (request.getEndsAt() != null) banner.setEndsAt(request.getEndsAt());
        if (banner.getStartsAt() != null && banner.getEndsAt() != null
                && banner.getStartsAt().isAfter(banner.getEndsAt())) {
            throw new GenericUncheckedException("Banner start must be before end", "BANNER_WINDOW_INVALID", BAD_REQUEST);
        }
        if (request.getActive() != null) banner.setIsActive(request.getActive());
        else if (banner.getId() == null) banner.setIsActive(Boolean.TRUE);
    }

    private Banner findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(bannerRepo, uuid, "Banner not found with uuid: " + uuid, "BANNER_NOT_FOUND");
    }
}
