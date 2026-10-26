package com.bikalpa.nepmart.core.role.dto.request;

import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Filters by related entity ids. All are optional; null means "no filter".
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleSearchRequestDto extends BasePaginationDto {

    private UUID userUuid;

    private UUID roleUuid;

    private UUID vendorUuid;
}
