package com.bikalpa.nepmart.core.permission.utility;

import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.permission.entity.Permission;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class PermissionConverter {

    public static PermissionResponseDto toResponseDto(Permission permission) {
        if (permission == null) {
            return null;
        }
        PermissionResponseDto response = new PermissionResponseDto();
        response.setUuid(permission.getUuid());
        response.setIsActive(permission.getIsActive());
        response.setName(permission.getName());
        response.setCode(permission.getCode());
        response.setModule(permission.getModule());
        response.setAction(permission.getAction());
        return response;
    }
}
