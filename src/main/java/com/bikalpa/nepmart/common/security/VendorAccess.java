package com.bikalpa.nepmart.common.security;

import com.bikalpa.nepmart.core.auth.utility.JwtUtility;
import com.bikalpa.nepmart.core.permission.repo.RolePermissionRepo;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserType;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Central vendor-isolation guard. Never trust a vendor id from the client:
 * every vendor-scoped write resolves the vendor server side and checks that
 * the caller is a platform admin or holds a role on that vendor.
 *
 * <p>Vendor A can never touch Vendor B's private data because every service
 * funnels through {@link #requireVendorAccess} before touching vendor rows.
 */
@Component
@RequiredArgsConstructor
public class VendorAccess {

    private final JwtUtility jwtUtility;
    private final UserRoleRepo userRoleRepo;
    private final RolePermissionRepo rolePermissionRepo;

    public User currentUser() {
        User user = jwtUtility.getCurrentUser().orElse(null);
        if (user == null) {
            throw new GenericUncheckedException("Authentication required", "AUTHENTICATION_REQUIRED", UNAUTHORIZED);
        }
        return user;
    }

    public boolean isPlatformAdmin(User user) {
        if (user.getUserType() == UserType.ADMIN) {
            return true;
        }
        return userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId()).stream()
                .anyMatch(ur -> ur.getRole().getScope().name().equals("SYSTEM")
                        && "SUPER_ADMIN".equals(ur.getRole().getCode()));
    }

    public boolean hasPermission(User user, String permissionCode) {
        List<UserRole> assignments = userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId());
        for (UserRole assignment : assignments) {
            if (rolePermissionRepo.existsByRoleIdAndPermissionCode(assignment.getRole().getId(), permissionCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVendorMember(User user, Vendor vendor) {
        return userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId()).stream()
                .anyMatch(ur -> ur.getVendor() != null && ur.getVendor().getId().equals(vendor.getId()));
    }

    /**
     * Throws 403 unless the caller is a platform admin or a member of the vendor.
     */
    public void requireVendorAccess(Vendor vendor) {
        User user = currentUser();
        if (isPlatformAdmin(user)) {
            return;
        }
        if (!isVendorMember(user, vendor)) {
            throw new GenericUncheckedException(
                    "You do not have access to this vendor", "VENDOR_ACCESS_DENIED", FORBIDDEN);
        }
    }

    /**
     * First vendor the user belongs to, for endpoints where the client does not
     * (and must not) pick the vendor itself.
     */
    public java.util.Optional<Vendor> firstVendorOf(User user) {
        return userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId()).stream()
                .filter(ur -> ur.getVendor() != null)
                .map(UserRole::getVendor)
                .findFirst();
    }
}
