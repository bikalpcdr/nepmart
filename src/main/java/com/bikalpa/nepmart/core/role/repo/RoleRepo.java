package com.bikalpa.nepmart.core.role.repo;

import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.core.role.enums.RoleScope;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
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
public interface RoleRepo extends BaseRepository<Role> {

    Optional<Role> findByCode(String code);

    Optional<Role> findByCodeAndVendorId(String code, Integer vendorId);

    boolean existsByCodeAndVendor(String code, Vendor vendor);

    boolean existsByCodeAndVendorIsNull(String code);

    boolean existsByCodeAndVendorAndIdNot(String code, Vendor vendor, Integer id);

    boolean existsByCodeAndVendorIsNullAndIdNot(String code, Integer id);

    /**
     * Vendor-scoped uniqueness depends on the vendor, so the service picks the
     * matching check. System roles have no vendor and are unique on code alone.
     */
    default boolean existsByCodeAndVendorScope(String code, Vendor vendor) {
        return vendor == null
                ? existsByCodeAndVendorIsNull(code)
                : existsByCodeAndVendor(code, vendor);
    }

    /**
     * Same as {@link #existsByCodeAndVendorScope(String, Vendor)} but excludes the
     * role being updated, so an unchanged code is not treated as a collision.
     */
    default boolean existsByCodeAndVendorScopeAndIdNot(String code, Vendor vendor, Integer id) {
        return vendor == null
                ? existsByCodeAndVendorIsNullAndIdNot(code, id)
                : existsByCodeAndVendorAndIdNot(code, vendor, id);
    }

    @Query("""
            select r from Role r
            where (:name = '' or lower(r.name) like lower(concat('%', :name, '%') ) )
              and (:code = '' or lower(r.code) like lower(concat('%', :code, '%') ) )
              and (:scope is null or r.scope = :scope)
            """)
    Page<Role> searchRoles(
            @Param("name") String name,
            @Param("code") String code,
            @Param("scope") RoleScope scope,
            Pageable pageable
    );
}
