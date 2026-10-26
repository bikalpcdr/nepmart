package com.bikalpa.nepmart.core.permission.repo;

import com.bikalpa.nepmart.core.permission.entity.RolePermission;
import com.bikalpa.nepmart.core.permission.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Join table between {@code roles} and {@code permissions}.
 *
 * <p>Keyed by the composite {@link RolePermissionId} rather than extending
 * {@code BaseRepository} - the join entity has no uuid of its own.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Repository
public interface RolePermissionRepo extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    @Query("""
            select case when count(rp) > 0 then true else false end
              from RolePermission rp
             where rp.role.id = :roleId
               and rp.permission.code = :code
               and rp.permission.isActive = true
            """)
    boolean existsByRoleIdAndPermissionCode(@Param("roleId") Integer roleId,
                                            @Param("code") String code);

    void deleteByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    List<RolePermission> findByRoleId(Integer roleId);

    /**
     * Distinct active permissions reachable through the given active roles,
     * fetched eagerly in a single query (no per-role or per-permission selects).
     */
    @Query("""
            select distinct rp from RolePermission rp
              join fetch rp.permission p
              join fetch rp.role r
             where r.id in :roleIds
               and r.isActive = true
               and p.isActive = true
            """)
    List<RolePermission> findActiveByRoleIdsWithPermission(@Param("roleIds") List<Integer> roleIds);
}
