package com.bikalpa.nepmart.core.role.dto.response;

import com.bikalpa.nepmart.core.role.enums.RoleScope;
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
public class RoleResponseDto extends BaseDto {

    private String name;

    private String code;

    private RoleScope scope;

    private Boolean isSystemRole;

    /* Null for SYSTEM-scoped roles. */
    private UUID vendorUuid;

    private String vendorName;
}
