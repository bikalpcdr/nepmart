package com.bikalpa.nepmart.core.user.utility;

import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import com.bikalpa.nepmart.core.user.entity.User;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class UserConverter {

    public static UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto response = new UserResponseDto();
        response.setUuid(user.getUuid());
        response.setIsActive(user.getIsActive());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setUserType(user.getUserType());
        return response;
    }
}
