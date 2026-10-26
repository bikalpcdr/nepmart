package com.bikalpa.nepmart.core.vendor.dto.response;

import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
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
public class VendorResponseDto extends BaseDto {
    private String name;

    private String slug;

    private VendorStatus status;

    private String description;

    private String logoUrl;

    private String contactEmail;
}
