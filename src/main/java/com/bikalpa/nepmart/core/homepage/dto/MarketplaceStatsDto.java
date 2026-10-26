package com.bikalpa.nepmart.core.homepage.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketplaceStatsDto {
    private long productCount;
    private long vendorCount;
    private long categoryCount;
    private long orderCount;
}
