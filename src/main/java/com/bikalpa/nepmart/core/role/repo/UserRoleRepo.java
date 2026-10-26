package com.bikalpa.nepmart.core.role.repo;

import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Assignment of a {@code Role} to a {@code User}, optionally scoped to a vendor.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Repository
public interface UserRoleRepo extends BaseRepository<UserRole> {

    /**
     * Duplicate detection is vendor-scoped, so the service picks the matching check.
     * A vendor-less assignment (system role) is unique on {@code (user, role)} alone.
     */
    boolean existsByUserIdAndRoleIdAndVendor(Integer userId, Integer roleId, Vendor vendor);

    boolean existsByUserIdAndRoleIdAndVendorIsNull(Integer userId, Integer roleId);

    default boolean existsByUserRoleVendor(Integer userId, Integer roleId, Vendor vendor) {
        return vendor == null
                ? existsByUserIdAndRoleIdAndVendorIsNull(userId, roleId)
                : existsByUserIdAndRoleIdAndVendor(userId, roleId, vendor);
    }

    List<UserRole> findByUserId(Integer userId);

    List<UserRole> findByRoleId(Integer roleId);

    /**
     * Active role assignments for the init payload, with role + vendors fetched
     * eagerly in one query so mapping never triggers N+1 selects.
     */
    @Query("""
            select distinct ur from UserRole ur
              join fetch ur.role r
              left join fetch r.vendor rv
              left join fetch ur.vendor uv
             where ur.user.id = :userId
               and ur.isActive = true
               and r.isActive = true
            """)
    List<UserRole> findActiveByUserIdWithRoleAndVendors(@Param("userId") Integer userId);

    @Query("""
            select ur from UserRole ur
            where (:userId is null or ur.user.id = :userId)
              and (:roleId is null or ur.role.id = :roleId)
              and (:vendorId is null or ur.vendor.id = :vendorId)
            """)
    Page<UserRole> searchUserRoles(
            @Param("userId") Integer userId,
            @Param("roleId") Integer roleId,
            @Param("vendorId") Integer vendorId,
            Pageable pageable
    );
}
