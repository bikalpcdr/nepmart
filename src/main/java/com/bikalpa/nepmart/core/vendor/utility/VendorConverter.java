package com.bikalpa.nepmart.core.vendor.utility;

import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class VendorConverter {

    public static VendorResponseDto toResponseDto(Vendor vendor) {
        if (vendor == null) {
            return null;
        }
        VendorResponseDto response = new VendorResponseDto();
        response.setUuid(vendor.getUuid());
        response.setIsActive(vendor.getIsActive());
        response.setName(vendor.getName());
        response.setSlug(vendor.getSlug());
        response.setStatus(vendor.getStatus());
        response.setDescription(vendor.getDescription());
        response.setLogoUrl(vendor.getLogoUrl());
        response.setContactEmail(vendor.getContactEmail());
        return response;
    }
}
