package com.bikalpa.nepmart.core.role.service.impl;

import com.bikalpa.nepmart.core.role.dto.request.UserRoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.UserRoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.UserRoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.enums.RoleScope;
import com.bikalpa.nepmart.core.role.repo.RoleRepo;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.role.service.UserRoleService;
import com.bikalpa.nepmart.core.role.utility.UserRoleConverter;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.repo.VendorRepo;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;
import static com.bikalpa.nepmart.constant.PageConstant.*;
import static org.springframework.http.HttpStatus.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "createdAt", "updatedAt"
    );

    private final UserRoleRepo userRoleRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final VendorRepo vendorRepo;

    @Override
    public UserRoleResponseDto assignRole(UserRoleRequestDto request) {
        User user = EntityLookup.findByUuid(userRepo, request.getUserUuid(),
                USER_NOT_FOUND_MESSAGE + request.getUserUuid(), USER_NOT_FOUND);

        Role role = EntityLookup.findByUuid(roleRepo, request.getRoleUuid(),
                ROLE_NOT_FOUND_MESSAGE + request.getRoleUuid(), ROLE_NOT_FOUND);

        Vendor vendor = resolveVendorForScope(role.getScope(), request.getVendorUuid());

        if (userRoleRepo.existsByUserRoleVendor(user.getId(), role.getId(), vendor)) {
            throw new GenericUncheckedException(USER_ROLE_ALREADY_ASSIGNED_MESSAGE, USER_ROLE_ALREADY_ASSIGNED, CONFLICT);
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setVendor(vendor);
        userRole.setIsActive(Boolean.TRUE);

        userRoleRepo.save(userRole);
        log.info("Role {} assigned to user {}", role.getCode(), user.getEmail());

        return UserRoleConverter.toResponseDto(userRole);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<UserRoleResponseDto> getUserRoles(UserRoleSearchRequestDto request) {
        Pageable pageable = buildPageable(request);

        Integer userId = resolveUserId(request.getUserUuid());
        Integer roleId = resolveRoleId(request.getRoleUuid());
        Integer vendorId = resolveVendorId(request.getVendorUuid());

        Page<UserRoleResponseDto> result = userRoleRepo.searchUserRoles(
                userId,
                roleId,
                vendorId,
                pageable
        ).map(UserRoleConverter::toResponseDto);

        return PaginationMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponseDto getUserRole(UUID uuid) {
        return UserRoleConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    public void removeUserRole(UUID uuid) {
        UserRole userRole = findByUuidOrThrow(uuid);
        userRole.setIsActive(Boolean.FALSE);
        userRoleRepo.save(userRole);

        log.info("User role assignment removed with uuid {}", uuid);
    }

    /**
     * A VENDOR-scoped role must reference a vendor; a SYSTEM-scoped role must not.
     * Mirrors {@code RoleServiceImpl} so the scope rules stay consistent.
     */
    private Vendor resolveVendorForScope(RoleScope scope, UUID vendorUuid) {
        if (scope == null) {
            throw new GenericUncheckedException(ROLE_SCOPE_REQUIRED_MESSAGE, ROLE_SCOPE_REQUIRED, BAD_REQUEST);
        }

        if (scope == RoleScope.SYSTEM) {
            if (vendorUuid != null) {
                throw new GenericUncheckedException(ROLE_VENDOR_NOT_ALLOWED_MESSAGE, ROLE_VENDOR_NOT_ALLOWED, BAD_REQUEST);
            }
            return null;
        }

        /* RoleScope.VENDOR */
        if (vendorUuid == null) {
            throw new GenericUncheckedException(ROLE_VENDOR_REQUIRED_MESSAGE, ROLE_VENDOR_REQUIRED, BAD_REQUEST);
        }

        return EntityLookup.findByUuid(vendorRepo, vendorUuid, VENDOR_NOT_FOUND_MESSAGE + vendorUuid, VENDOR_NOT_FOUND);
    }

    /**
     * Search filters are optional; a supplied uuid must resolve or the request is
     * rejected, so a bad uuid is surfaced instead of silently ignored.
     */
    private Integer resolveUserId(UUID userUuid) {
        if (userUuid == null) {
            return null;
        }
        return userRepo.findByUuid(userUuid).map(User::getId).orElseThrow(
                () -> new GenericUncheckedException(USER_NOT_FOUND_MESSAGE + userUuid, USER_NOT_FOUND, NOT_FOUND));
    }

    private Integer resolveRoleId(UUID roleUuid) {
        if (roleUuid == null) {
            return null;
        }
        return roleRepo.findByUuid(roleUuid).map(Role::getId).orElseThrow(
                () -> new GenericUncheckedException(ROLE_NOT_FOUND_MESSAGE + roleUuid, ROLE_NOT_FOUND, NOT_FOUND));
    }

    private Integer resolveVendorId(UUID vendorUuid) {
        if (vendorUuid == null) {
            return null;
        }
        return vendorRepo.findByUuid(vendorUuid).map(Vendor::getId).orElseThrow(
                () -> new GenericUncheckedException(VENDOR_NOT_FOUND_MESSAGE + vendorUuid, VENDOR_NOT_FOUND, NOT_FOUND));
    }

    private UserRole findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(userRoleRepo, uuid, USER_ROLE_NOT_FOUND_MESSAGE + uuid, USER_ROLE_NOT_FOUND);
    }

    private Pageable buildPageable(UserRoleSearchRequestDto request) {
        int page = request.getPage() == null ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null ? DEFAULT_SIZE : request.getSize();

        if (page < DEFAULT_PAGE) {
            throw new GenericUncheckedException(INVALID_PAGE_MESSAGE, INVALID_PAGE, BAD_REQUEST);
        }

        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new GenericUncheckedException(INVALID_PAGE_SIZE_MESSAGE, INVALID_PAGE_SIZE, BAD_REQUEST);
        }

        String sortBy = request.getSortBy() == null ? "createdAt" : request.getSortBy();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new GenericUncheckedException(
                    INVALID_SORT_FIELD_MESSAGE + sortBy,
                    INVALID_SORT_FIELD,
                    BAD_REQUEST
            );
        }

        String direction = request.getSortDirection() == null ? "DESC" : request.getSortDirection().toUpperCase(Locale.ROOT);

        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new GenericUncheckedException(INVALID_SORT_DIRECTION_MESSAGE, INVALID_SORT_DIRECTION, BAD_REQUEST);
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
    }
}
