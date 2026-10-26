package com.bikalpa.nepmart.core.vendor.repo;

import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Repository
public interface VendorRepo extends BaseRepository<Vendor> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Integer id);

    Optional<Vendor> findBySlug(String slug);

    java.util.List<Vendor> findByIsActiveTrueAndStatusOrderByNameAsc(VendorStatus status);

    @Query("""
            select v from Vendor v
            where (:name = '' or lower(v.name) like lower(concat('%', :name, '%') ) )
              and (:slug = '' or lower(v.slug) like lower(concat('%', :slug, '%') ) )
              and (:status is null or v.status = :status)
            """)
    Page<Vendor> searchVendors(
            @Param("name") String name,
            @Param("slug") String slug,
            @Param("status") VendorStatus status,
            Pageable pageable
    );
}
