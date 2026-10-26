package com.bikalpa.nepmart.core.role.utility;

import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.Role;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class RoleConverter {

    public static RoleResponseDto toResponseDto(Role role) {
        if (role == null) {
            return null;
        }
        RoleResponseDto response = new RoleResponseDto();
        response.setUuid(role.getUuid());
        response.setIsActive(role.getIsActive());
        response.setName(role.getName());
        response.setCode(role.getCode());
        response.setScope(role.getScope());
        response.setIsSystemRole(role.getIsSystemRole());

        if (role.getVendor() != null) {
            response.setVendorUuid(role.getVendor().getUuid());
            response.setVendorName(role.getVendor().getName());
        }

        return response;
    }
}
