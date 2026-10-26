package com.bikalpa.nepmart.core.permission.repo;

import com.bikalpa.nepmart.core.permission.entity.Permission;
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
public interface PermissionRepo extends BaseRepository<Permission> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Integer id);

    Optional<Permission> findByCode(String code);

    @Query("""
            select p from Permission p
            where (:name = '' or lower(p.name) like lower(concat('%', :name, '%') ) )
              and (:code = '' or lower(p.code) like lower(concat('%', :code, '%') ) )
              and (:module = '' or lower(p.module) = lower(:module) )
              and (:action = '' or lower(p.action) = lower(:action) )
            """)
    Page<Permission> searchPermissions(
            @Param("name") String name,
            @Param("code") String code,
            @Param("module") String module,
            @Param("action") String action,
            Pageable pageable
    );
}
