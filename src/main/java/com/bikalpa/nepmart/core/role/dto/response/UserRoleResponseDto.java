package com.bikalpa.nepmart.core.role.dto.response;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResponseDto extends BaseDto {

    private UUID userUuid;

    private String userEmail;

    private UUID roleUuid;

    private String roleCode;

    private String roleName;

    /* Null for assignments of SYSTEM-scoped roles. */
    private UUID vendorUuid;

    private String vendorName;
}
