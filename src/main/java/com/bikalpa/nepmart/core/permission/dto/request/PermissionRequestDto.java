package com.bikalpa.nepmart.core.permission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequestDto {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 150)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String module;

    @NotBlank
    @Size(max = 50)
    private String action;
}
