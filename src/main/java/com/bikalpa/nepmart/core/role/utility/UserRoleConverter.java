package com.bikalpa.nepmart.core.role.utility;

import com.bikalpa.nepmart.core.role.dto.response.UserRoleResponseDto;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class UserRoleConverter {

    public static UserRoleResponseDto toResponseDto(UserRole userRole) {
        if (userRole == null) {
            return null;
        }
        UserRoleResponseDto response = new UserRoleResponseDto();
        response.setUuid(userRole.getUuid());
        response.setIsActive(userRole.getIsActive());

        if (userRole.getUser() != null) {
            response.setUserUuid(userRole.getUser().getUuid());
            response.setUserEmail(userRole.getUser().getEmail());
        }

        if (userRole.getRole() != null) {
            response.setRoleUuid(userRole.getRole().getUuid());
            response.setRoleCode(userRole.getRole().getCode());
            response.setRoleName(userRole.getRole().getName());
        }

        if (userRole.getVendor() != null) {
            response.setVendorUuid(userRole.getVendor().getUuid());
            response.setVendorName(userRole.getVendor().getName());
        }

        return response;
    }
}
