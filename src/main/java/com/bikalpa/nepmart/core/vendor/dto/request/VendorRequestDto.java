package com.bikalpa.nepmart.core.vendor.dto.request;

import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class VendorRequestDto {

    @NotBlank
    @Size(max = 150)
    private String name;

    /* Optional: when omitted it is derived from {@link #name}. */
    @Size(max = 180)
    private String slug;

    private VendorStatus status;

    private String description;

    @Size(max = 500)
    private String logoUrl;

    @Email
    private String contactEmail;
}
