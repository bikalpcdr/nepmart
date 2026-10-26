package com.bikalpa.nepmart.core.vendor.service;

import com.bikalpa.nepmart.core.vendor.dto.request.VendorRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.request.VendorSearchRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
public interface VendorService {
    void createVendor(VendorRequestDto request);

    VendorResponseDto getVendor(UUID uuid);

    BasePaginationResponseDto<VendorResponseDto> getVendors(VendorSearchRequestDto request);

    java.util.List<VendorResponseDto> getPublicVendors();

    void updateVendor(UUID uuid, VendorRequestDto request);

    VendorResponseDto getVendorBySlug(String slug);

    VendorResponseDto getMyVendor();

    void updateVendorStatus(UUID uuid, com.bikalpa.nepmart.core.vendor.enums.VendorStatus status);

    void deleteVendor(UUID uuid);

    Vendor getEntity(UUID uuid);
}
