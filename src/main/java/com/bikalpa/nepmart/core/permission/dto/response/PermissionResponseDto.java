package com.bikalpa.nepmart.core.permission.dto.response;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
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
public class PermissionResponseDto extends BaseDto {

    private String name;

    private String code;

    private String module;

    private String action;
}
