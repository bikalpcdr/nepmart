package com.bikalpa.nepmart.core.vendor.dto.request;

import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
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
public class VendorSearchRequestDto extends BasePaginationDto {
    private String name;

    private String slug;

    private VendorStatus status;
}
