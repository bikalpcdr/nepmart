package com.bikalpa.nepmart.core.permission.dto.request;

import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
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
public class PermissionSearchRequestDto extends BasePaginationDto {

    private String name;

    private String code;

    private String module;

    private String action;
}
