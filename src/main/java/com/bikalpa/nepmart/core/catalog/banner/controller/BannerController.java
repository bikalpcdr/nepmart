package com.bikalpa.nepmart.core.catalog.banner.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.catalog.banner.dto.request.BannerRequestDto;
import com.bikalpa.nepmart.core.catalog.banner.dto.response.BannerResponseDto;
import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.core.catalog.banner.service.BannerService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

@RestController
@RequestMapping("/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/live")
    @SuccessMessage(action = FETCHED, entity = BANNER)
    public List<BannerResponseDto> live(@RequestParam(required = false) BannerPlacement placement) {
        return bannerService.getLiveBanners(placement);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('banner:read')")
    @SuccessMessage(action = FETCHED, entity = BANNER)
    public BasePaginationResponseDto<BannerResponseDto> getAll(
            @RequestParam(required = false) BannerPlacement placement,
            @Valid @ModelAttribute BasePaginationDto page) {
        return bannerService.getBanners(placement, page);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('banner:read')")
    @SuccessMessage(action = FETCHED, entity = BANNER)
    public BannerResponseDto getById(@PathVariable UUID uuid) {
        return bannerService.getBanner(uuid);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('banner:create')")
    @SuccessMessage(action = CREATED, entity = BANNER)
    public void create(@Valid @RequestBody BannerRequestDto request) {
        bannerService.createBanner(request);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('banner:update')")
    @SuccessMessage(action = UPDATED, entity = BANNER)
    public void update(@PathVariable UUID uuid, @RequestBody BannerRequestDto request) {
        bannerService.updateBanner(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('banner:delete')")
    @SuccessMessage(action = DELETED, entity = BANNER)
    public void delete(@PathVariable UUID uuid) {
        bannerService.deleteBanner(uuid);
    }
}
