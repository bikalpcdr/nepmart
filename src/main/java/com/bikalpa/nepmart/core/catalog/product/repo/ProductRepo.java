package com.bikalpa.nepmart.core.catalog.product.repo;

import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Repository
public interface ProductRepo extends BaseRepository<Product> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Integer id);

    boolean existsByVendorIdAndSku(Integer vendorId, String sku);

    boolean existsByVendorIdAndSkuAndIdNot(Integer vendorId, String sku, Integer id);

    Optional<Product> findBySlug(String slug);

    List<Product> findTop12ByIsActiveTrueAndStatusAndFeaturedTrueOrderByUpdatedAtDesc(ProductStatus status);

    long countByIsActiveTrueAndStatus(ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.uuid = :uuid")
    Optional<Product> findByUuidForUpdate(@Param("uuid") java.util.UUID uuid);

    @Query("""
            select p from Product p
              left join fetch p.vendor v
              left join fetch p.category c
            where (:search = '' or lower(p.name) like lower(concat('%', :search, '%'))
               or lower(p.description) like lower(concat('%', :search, '%')))
              and (:categorySlug = '' or (c is not null and lower(c.slug) = lower(:categorySlug)))
              and (:vendorSlug = '' or lower(v.slug) = lower(:vendorSlug))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:status is null or p.status = :status)
              and (:featured is null or p.featured = :featured)
            """)
    Page<Product> searchProducts(
            @Param("search") String search,
            @Param("categorySlug") String categorySlug,
            @Param("vendorSlug") String vendorSlug,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") ProductStatus status,
            @Param("featured") Boolean featured,
            Pageable pageable
    );
}
