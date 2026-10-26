package com.bikalpa.nepmart.core.homepage.dto;

import com.bikalpa.nepmart.core.catalog.banner.dto.response.BannerResponseDto;
import com.bikalpa.nepmart.core.catalog.category.dto.response.CategoryResponseDto;
import com.bikalpa.nepmart.core.catalog.product.dto.response.ProductResponseDto;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomepageResponseDto {
    private List<ProductResponseDto> featuredProducts;
    private List<CategoryResponseDto> categories;
    private List<BannerResponseDto> heroBanners;
    private List<BannerResponseDto> promoBanners;
    private List<VendorResponseDto> featuredVendors;
    private MarketplaceStatsDto stats;
}
