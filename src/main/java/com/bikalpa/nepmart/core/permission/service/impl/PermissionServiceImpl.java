package com.bikalpa.nepmart.core.permission.service.impl;

import com.bikalpa.nepmart.core.permission.dto.request.PermissionRequestDto;
import com.bikalpa.nepmart.core.permission.dto.request.PermissionSearchRequestDto;
import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.permission.entity.Permission;
import com.bikalpa.nepmart.core.permission.repo.PermissionRepo;
import com.bikalpa.nepmart.core.permission.service.PermissionService;
import com.bikalpa.nepmart.core.permission.utility.PermissionConverter;
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
public class PermissionServiceImpl implements PermissionService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "code", "module", "action", "createdAt", "updatedAt"
    );

    private final PermissionRepo permissionRepo;

    @Override
    public void createPermission(PermissionRequestDto request) {
        String code = normalizeCode(request.getCode());

        if (permissionRepo.existsByCode(code)) {
            throw new GenericUncheckedException(PERMISSION_CODE_ALREADY_EXISTS_MESSAGE, PERMISSION_CODE_ALREADY_EXISTS, CONFLICT);
        }

        Permission permission = new Permission();
        permission.setName(request.getName().trim());
        permission.setCode(code);
        permission.setModule(request.getModule().trim());
        permission.setAction(request.getAction().trim().toUpperCase(Locale.ROOT));
        permission.setIsActive(Boolean.TRUE);

        permissionRepo.save(permission);
        log.info("Permission created successfully with code {}", code);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDto getPermission(UUID uuid) {
        return PermissionConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<PermissionResponseDto> getPermissions(PermissionSearchRequestDto request) {
        Pageable pageable = buildPageable(request);

        Page<PermissionResponseDto> result = permissionRepo.searchPermissions(
                normalizeSearchParam(request.getName()),
                normalizeSearchParam(request.getCode()),
                normalizeSearchParam(request.getModule()),
                normalizeSearchParam(request.getAction()),
                pageable
        ).map(PermissionConverter::toResponseDto);

        return PaginationMapper.toResponse(result);
    }

    @Override
    public void updatePermission(UUID uuid, PermissionRequestDto request) {
        Permission permission = findByUuidOrThrow(uuid);
        String code = normalizeCode(request.getCode());

        if (permissionRepo.existsByCodeAndIdNot(code, permission.getId())) {
            throw new GenericUncheckedException(PERMISSION_CODE_ALREADY_EXISTS_MESSAGE, PERMISSION_CODE_ALREADY_EXISTS, CONFLICT);
        }

        permission.setName(request.getName().trim());
        permission.setCode(code);
        permission.setModule(request.getModule().trim());
        permission.setAction(request.getAction().trim().toUpperCase(Locale.ROOT));

        permissionRepo.save(permission);
        log.info("Permission updated successfully with uuid {}", uuid);
    }

    @Override
    public void deletePermission(UUID uuid) {
        Permission permission = findByUuidOrThrow(uuid);
        permission.setIsActive(Boolean.FALSE);
        permissionRepo.save(permission);

        log.info("Permission soft-deleted successfully with uuid {}", uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new GenericUncheckedException(PERMISSION_CODE_REQUIRED_MESSAGE, PERMISSION_CODE_REQUIRED, BAD_REQUEST);
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeSearchParam(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    private Permission findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(permissionRepo, uuid, PERMISSION_NOT_FOUND_MESSAGE + uuid, PERMISSION_NOT_FOUND);
    }

    private Pageable buildPageable(PermissionSearchRequestDto request) {
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
