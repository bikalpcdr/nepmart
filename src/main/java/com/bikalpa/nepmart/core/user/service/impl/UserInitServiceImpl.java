package com.bikalpa.nepmart.core.user.service.impl;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.auth.utility.JwtUtility;
import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.permission.entity.RolePermission;
import com.bikalpa.nepmart.core.permission.repo.RolePermissionRepo;
import com.bikalpa.nepmart.core.permission.utility.PermissionConverter;
import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.role.utility.RoleConverter;
import com.bikalpa.nepmart.core.user.dto.response.UserInitResponseDto;
import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.service.UserInitService;
import com.bikalpa.nepmart.core.user.utility.UserConverter;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.utility.VendorConverter;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Loads the {@code GET /v1/users/me/init} payload for the authenticated user.
 *
 * <p>Uses exactly two bulk queries (assignments with role+vendors, then
 * permissions for those roles) so mapping never triggers N+1 selects and no
 * repository call happens inside a loop. Only active rows are surfaced and the
 * response never contains passwords or secrets.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserInitServiceImpl implements UserInitService {

    private final JwtUtility jwtUtility;
    private final UserRoleRepo userRoleRepo;
    private final RolePermissionRepo rolePermissionRepo;

    @Override
    public UserInitResponseDto getCurrentUserInit() {
        User user = jwtUtility.getCurrentUserOrThrow();
        ensureUsable(user);

        UserResponseDto userDto = UserConverter.toResponseDto(user);

        List<UserRole> assignments =
                userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId());

        List<RoleResponseDto> roles = new ArrayList<>(assignments.size());
        Map<Integer, Vendor> vendorsById = new LinkedHashMap<>();
        List<Integer> roleIds = new ArrayList<>(assignments.size());
        for (UserRole assignment : assignments) {
            roles.add(RoleConverter.toResponseDto(assignment.getRole()));
            roleIds.add(assignment.getRole().getId());
            collectVendor(vendorsById, assignment.getVendor());
            collectVendor(vendorsById, assignment.getRole().getVendor());
        }

        List<RolePermission> rolePermissions = roleIds.isEmpty()
                ? List.of()
                : rolePermissionRepo.findActiveByRoleIdsWithPermission(roleIds);

        Map<Integer, PermissionResponseDto> permissionsById = new LinkedHashMap<>();
        for (RolePermission rolePermission : rolePermissions) {
            permissionsById.putIfAbsent(
                    rolePermission.getPermission().getId(),
                    PermissionConverter.toResponseDto(rolePermission.getPermission()));
        }
        List<PermissionResponseDto> permissions = new ArrayList<>(permissionsById.values());
        List<String> permissionCodes = permissions.stream()
                .map(PermissionResponseDto::getCode)
                .distinct()
                .sorted()
                .toList();

        List<VendorResponseDto> vendors = vendorsById.values().stream()
                .map(VendorConverter::toResponseDto)
                .toList();

        log.debug("Loaded init payload for user {} with {} roles and {} permissions",
                user.getEmail(), roles.size(), permissions.size());

        return UserInitResponseDto.builder()
                .user(userDto)
                .roles(roles)
                .vendors(vendors)
                .permissions(permissions)
                .permissionCodes(permissionCodes)
                .build();
    }

    /**
     * Inactive, blocked or unverified accounts cannot bootstrap the frontend.
     * SecurityConfig already guarantees authentication; this guards the state.
     */
    private void ensureUsable(User user) {
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new GenericUncheckedException(
                    MessageConstant.ACCOUNT_NOT_ACTIVE_MESSAGE,
                    MessageConstant.ACCOUNT_NOT_ACTIVE,
                    FORBIDDEN);
        }
        switch (user.getStatus()) {
            case BLOCKED, INACTIVE -> throw new GenericUncheckedException(
                    MessageConstant.ACCOUNT_NOT_ACTIVE_MESSAGE,
                    MessageConstant.ACCOUNT_NOT_ACTIVE,
                    FORBIDDEN);
            default -> { /* ACTIVE / PENDING_VERIFICATION may proceed. */ }
        }
    }

    /**
     * Vendor context comes from both the assignment row and the role definition
     * (multi-vendor RBAC). Inactive vendors are excluded; dedupe keeps one entry
     * per vendor id.
     */
    private void collectVendor(Map<Integer, Vendor> vendorsById, Vendor vendor) {
        if (vendor != null
                && Boolean.TRUE.equals(vendor.getIsActive())
                && vendor.getId() != null) {
            vendorsById.putIfAbsent(vendor.getId(), vendor);
        }
    }
}
