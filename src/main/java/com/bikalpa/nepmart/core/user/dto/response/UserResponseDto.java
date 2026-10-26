package com.bikalpa.nepmart.core.user.dto.response;

import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.user.enums.UserType;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto extends BaseDto {
    private String email;
    private UserStatus status;
    private UserType userType;
}
