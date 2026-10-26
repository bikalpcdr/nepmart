package com.bikalpa.nepmart.core.role.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class UserRoleRequestDto {

    @NotNull
    private UUID userUuid;

    @NotNull
    private UUID roleUuid;

    /**
     * Required when the role is VENDOR-scoped; must be null for SYSTEM-scoped roles.
     * Validated against the role's scope by the service.
     */
    private UUID vendorUuid;
}
