package com.bikalpa.nepmart.core.role.service.impl;

import com.bikalpa.nepmart.core.role.dto.request.RoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.RoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.core.role.enums.RoleScope;
import com.bikalpa.nepmart.core.role.repo.RoleRepo;
import com.bikalpa.nepmart.core.role.service.RoleService;
import com.bikalpa.nepmart.core.role.utility.RoleConverter;
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
public class RoleServiceImpl implements RoleService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "code", "scope", "createdAt", "updatedAt"
    );

    private final RoleRepo roleRepo;
    private final VendorRepo vendorRepo;

    @Override
    public void createRole(RoleRequestDto request) {
        String code = normalizeCode(request.getCode());
        Vendor vendor = resolveVendorForScope(request.getScope(), request.getVendorUuid());

        if (roleRepo.existsByCodeAndVendorScope(code, vendor)) {
            throw new GenericUncheckedException(ROLE_CODE_ALREADY_EXISTS_MESSAGE, ROLE_CODE_ALREADY_EXISTS, CONFLICT);
        }

        Role role = new Role();
        role.setName(request.getName().trim());
        role.setCode(code);
        role.setScope(request.getScope());
        role.setVendor(vendor);
        /* Roles created through the API are never system roles. */
        role.setIsSystemRole(Boolean.FALSE);
        role.setIsActive(Boolean.TRUE);

        roleRepo.save(role);
        log.info("Role created successfully with code {}", code);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRole(UUID uuid) {
        return RoleConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<RoleResponseDto> getRoles(RoleSearchRequestDto request) {
        Pageable pageable = buildPageable(request);

        Page<RoleResponseDto> result = roleRepo.searchRoles(
                normalizeSearchParam(request.getName()),
                normalizeSearchParam(request.getCode()),
                request.getScope(),
                pageable
        ).map(RoleConverter::toResponseDto);

        return PaginationMapper.toResponse(result);
    }

    @Override
    public void updateRole(UUID uuid, RoleRequestDto request) {
        Role role = findByUuidOrThrow(uuid);

        if (Boolean.TRUE.equals(role.getIsSystemRole())) {
            throw new GenericUncheckedException(SYSTEM_ROLE_IMMUTABLE_MESSAGE, SYSTEM_ROLE_IMMUTABLE, FORBIDDEN);
        }

        String code = normalizeCode(request.getCode());
        Vendor vendor = resolveVendorForScope(request.getScope(), request.getVendorUuid());

        if (roleRepo.existsByCodeAndVendorScopeAndIdNot(code, vendor, role.getId())) {
            throw new GenericUncheckedException(ROLE_CODE_ALREADY_EXISTS_MESSAGE, ROLE_CODE_ALREADY_EXISTS, CONFLICT);
        }

        role.setName(request.getName().trim());
        role.setCode(code);
        role.setScope(request.getScope());
        role.setVendor(vendor);

        roleRepo.save(role);
        log.info("Role updated successfully with uuid {}", uuid);
    }

    @Override
    public void deleteRole(UUID uuid) {
        Role role = findByUuidOrThrow(uuid);

        if (Boolean.TRUE.equals(role.getIsSystemRole())) {
            throw new GenericUncheckedException(SYSTEM_ROLE_IMMUTABLE_MESSAGE, SYSTEM_ROLE_IMMUTABLE, FORBIDDEN);
        }

        role.setIsActive(Boolean.FALSE);
        roleRepo.save(role);

        log.info("Role soft-deleted successfully with uuid {}", uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    /**
     * A VENDOR-scoped role must reference a vendor; a SYSTEM-scoped role must not.
     * Enforces the {@code uk_roles_vendor_code} unique constraint at the app layer.
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

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new GenericUncheckedException(ROLE_CODE_REQUIRED_MESSAGE, ROLE_CODE_REQUIRED, BAD_REQUEST);
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeSearchParam(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    private Role findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(roleRepo, uuid, ROLE_NOT_FOUND_MESSAGE + uuid, ROLE_NOT_FOUND);
    }

    private Pageable buildPageable(RoleSearchRequestDto request) {
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

