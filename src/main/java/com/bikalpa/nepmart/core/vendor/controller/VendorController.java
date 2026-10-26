package com.bikalpa.nepmart.core.vendor.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.vendor.dto.request.VendorRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.request.VendorSearchRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.core.vendor.service.VendorService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@RestController
@RequestMapping("/v1/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @SuccessMessage(action = CREATED, entity = VENDOR)
    public void create(@Valid @RequestBody VendorRequestDto request) {
        vendorService.createVendor(request);
    }

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = VENDOR)
    public BasePaginationResponseDto<VendorResponseDto> getAll(@Valid @ModelAttribute VendorSearchRequestDto request) {
        return vendorService.getVendors(request);
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = VENDOR)
    public VendorResponseDto getById(@PathVariable UUID uuid) {
        return vendorService.getVendor(uuid);
    }

    @GetMapping("/slug/{slug}")
    @SuccessMessage(action = FETCHED, entity = VENDOR)
    public VendorResponseDto getBySlug(@PathVariable String slug) {
        return vendorService.getVendorBySlug(slug);
    }

    @GetMapping("/me")
    @SuccessMessage(action = FETCHED, entity = VENDOR)
    public VendorResponseDto getMyVendor() {
        return vendorService.getMyVendor();
    }

    @PutMapping("/{uuid}")
    @SuccessMessage(action = UPDATED, entity = VENDOR)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody VendorRequestDto request) {
        vendorService.updateVendor(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @SuccessMessage(action = DELETED, entity = VENDOR)
    public void delete(@PathVariable UUID uuid) {
        vendorService.deleteVendor(uuid);
    }

    @PatchMapping("/admin/{uuid}/status")
    @PreAuthorize("hasAuthority('vendor:update')")
    @SuccessMessage(action = STATUS_UPDATED, entity = VENDOR)
    public void updateStatus(@PathVariable UUID uuid,
                             @Valid @RequestBody StatusUpdateRequest request) {
        vendorService.updateVendorStatus(uuid, request.status());
    }

    public record StatusUpdateRequest(@NotNull VendorStatus status) {
    }
}
