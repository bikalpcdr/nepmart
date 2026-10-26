package com.bikalpa.nepmart.core.role.dto.request;

import com.bikalpa.nepmart.core.role.enums.RoleScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RoleRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String code;

    @NotNull
    private RoleScope scope;

    /* Required only when scope = VENDOR; must be null when scope = SYSTEM. */
    private UUID vendorUuid;
}
