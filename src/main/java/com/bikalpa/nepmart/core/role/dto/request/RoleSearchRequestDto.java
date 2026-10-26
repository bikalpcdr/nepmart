package com.bikalpa.nepmart.core.role.dto.request;

import com.bikalpa.nepmart.core.role.enums.RoleScope;
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
public class RoleSearchRequestDto extends BasePaginationDto {

    private String name;

    private String code;

    private RoleScope scope;
}
