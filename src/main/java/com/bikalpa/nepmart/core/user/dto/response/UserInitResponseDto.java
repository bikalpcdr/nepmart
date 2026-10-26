package com.bikalpa.nepmart.core.user.dto.response;

import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Aggregated initialization payload for the currently authenticated user.
 *
 * <p>Backs {@code GET /v1/users/me/init} (served under {@code /api} context-path).
 * Contains only public data - never passwords or secrets - and only active
 * assignments: inactive users/roles/permissions/vendors are excluded upstream.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInitResponseDto {

    private UserResponseDto user;

    private List<RoleResponseDto> roles;

    private List<VendorResponseDto> vendors;

    private List<PermissionResponseDto> permissions;

    /* Distinct permission codes (e.g. {@code user:read}) for quick frontend checks. */
    private List<String> permissionCodes;
}
