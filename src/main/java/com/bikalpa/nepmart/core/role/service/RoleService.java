package com.bikalpa.nepmart.core.role.service;

import com.bikalpa.nepmart.core.role.dto.request.RoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.RoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
public interface RoleService {
    void createRole(RoleRequestDto request);

    RoleResponseDto getRole(UUID uuid);

    BasePaginationResponseDto<RoleResponseDto> getRoles(RoleSearchRequestDto request);

    void updateRole(UUID uuid, RoleRequestDto request);

    void deleteRole(UUID uuid);

    Role getEntity(UUID uuid);
}
