package com.bikalpa.nepmart.core.catalog.banner.repo;

import com.bikalpa.nepmart.core.catalog.banner.entity.Banner;
import com.bikalpa.nepmart.core.catalog.banner.enums.BannerPlacement;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Repository
public interface BannerRepo extends BaseRepository<Banner> {
    List<Banner> findByIsActiveTrueAndPlacementOrderByDisplayOrderAsc(BannerPlacement placement);

    @Query("""
            select b from Banner b
            where b.isActive = true
              and (b.startsAt is null or b.startsAt <= :now)
              and (b.endsAt is null or b.endsAt >= :now)
              and (:placement is null or b.placement = :placement)
            order by b.displayOrder asc
            """)
    List<Banner> findLiveBanners(@Param("placement") BannerPlacement placement, @Param("now") LocalDateTime now);

    @Query("""
            select b from Banner b
            where (:placement is null or b.placement = :placement)
            """)
    Page<Banner> searchBanners(@Param("placement") BannerPlacement placement, Pageable pageable);
}
