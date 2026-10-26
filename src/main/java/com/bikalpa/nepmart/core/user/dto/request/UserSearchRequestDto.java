package com.bikalpa.nepmart.core.user.dto.request;

import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
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
public class UserSearchRequestDto extends BasePaginationDto {
    private String email;

    private UserStatus status;
}
