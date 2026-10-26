package com.bikalpa.nepmart.core.role.service;

import com.bikalpa.nepmart.core.role.dto.request.UserRoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.UserRoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.UserRoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.UUID;

/**
 * Manages assignment of roles to users.
 *
 * <p>UserRole is a link entity: there is no update - a call either assigns a new
 * role or, via delete, unassigns it. Search accepts uuids and resolves them to
 * ids internally.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
public interface UserRoleService {

    UserRoleResponseDto assignRole(UserRoleRequestDto request);

    BasePaginationResponseDto<UserRoleResponseDto> getUserRoles(UserRoleSearchRequestDto request);

    UserRoleResponseDto getUserRole(UUID uuid);

    void removeUserRole(UUID uuid);
}
