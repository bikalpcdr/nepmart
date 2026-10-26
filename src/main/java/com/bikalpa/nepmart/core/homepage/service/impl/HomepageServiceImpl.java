package com.bikalpa.nepmart.core.homepage.service.impl;

import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.core.catalog.banner.service.BannerService;
import com.bikalpa.nepmart.core.catalog.category.service.CategoryService;
import com.bikalpa.nepmart.core.catalog.product.service.ProductService;
import com.bikalpa.nepmart.core.homepage.dto.HomepageResponseDto;
import com.bikalpa.nepmart.core.homepage.dto.MarketplaceStatsDto;
import com.bikalpa.nepmart.core.homepage.service.HomepageService;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.core.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomepageServiceImpl implements HomepageService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BannerService bannerService;
    private final VendorService vendorService;

    @Override
    public HomepageResponseDto getHomepage() {
        List<VendorResponseDto> vendors = vendorService.getPublicVendors();
        return HomepageResponseDto.builder()
                .featuredProducts(productService.getFeaturedProducts(12))
                .categories(categoryService.getActiveCategories())
                .heroBanners(bannerService.getLiveBanners(BannerPlacement.HERO))
                .promoBanners(bannerService.getLiveBanners(BannerPlacement.HOMEPAGE))
                .featuredVendors(vendors.size() > 8 ? vendors.subList(0, 8) : vendors)
                .stats(MarketplaceStatsDto.builder()
                        .productCount(productService.countPublished())
                        .vendorCount(vendors.size())
                        .categoryCount(categoryService.getActiveCategories().size())
                        .orderCount(0L)
                        .build())
                .build();
    }
}
