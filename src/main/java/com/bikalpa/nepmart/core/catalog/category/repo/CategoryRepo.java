package com.bikalpa.nepmart.core.catalog.category.repo;

import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import com.bikalpa.nepmart.core.catalog.category.enums.CategoryStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Repository
public interface CategoryRepo extends BaseRepository<Category> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Integer id);

    Optional<Category> findBySlug(String slug);

    List<Category> findByIsActiveTrueAndStatusOrderByDisplayOrderAsc(CategoryStatus status);

    @Query("""
            select c from Category c
            where (:name = '' or lower(c.name) like lower(concat('%', :name, '%')))
              and (:status is null or c.status = :status)
              and (:rootOnly = false or c.parent is null)
            """)
    Page<Category> searchCategories(
            @Param("name") String name,
            @Param("status") CategoryStatus status,
            @Param("rootOnly") boolean rootOnly,
            Pageable pageable
    );
}
