package com.bikalpa.nepmart.core.permission.service;

import com.bikalpa.nepmart.core.permission.dto.request.PermissionRequestDto;
import com.bikalpa.nepmart.core.permission.dto.request.PermissionSearchRequestDto;
import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.permission.entity.Permission;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
public interface PermissionService {
    void createPermission(PermissionRequestDto request);

    PermissionResponseDto getPermission(UUID uuid);

    BasePaginationResponseDto<PermissionResponseDto> getPermissions(PermissionSearchRequestDto request);

    void updatePermission(UUID uuid, PermissionRequestDto request);

    void deletePermission(UUID uuid);

    Permission getEntity(UUID uuid);
}
