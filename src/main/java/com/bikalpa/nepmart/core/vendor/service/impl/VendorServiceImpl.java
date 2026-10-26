package com.bikalpa.nepmart.core.vendor.service.impl;

import com.bikalpa.nepmart.core.vendor.dto.request.VendorRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.request.VendorSearchRequestDto;
import com.bikalpa.nepmart.core.vendor.dto.response.VendorResponseDto;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.enums.VendorStatus;
import com.bikalpa.nepmart.common.security.VendorAccess;
import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.enums.RoleScope;
import com.bikalpa.nepmart.core.role.repo.RoleRepo;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.vendor.entity.Vendor;
import com.bikalpa.nepmart.core.vendor.membership.entity.VendorMembership;
import com.bikalpa.nepmart.core.vendor.membership.enums.MembershipStatus;
import com.bikalpa.nepmart.core.vendor.membership.repo.VendorMembershipRepo;
import com.bikalpa.nepmart.core.vendor.repo.VendorRepo;
import com.bikalpa.nepmart.core.vendor.service.VendorService;
import com.bikalpa.nepmart.core.vendor.utility.VendorConverter;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;
import static com.bikalpa.nepmart.constant.PageConstant.*;
import static org.springframework.http.HttpStatus.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VendorServiceImpl implements VendorService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "slug", "status", "createdAt", "updatedAt"
    );

    private final VendorRepo vendorRepo;
    private final VendorMembershipRepo membershipRepo;
    private final RoleRepo roleRepo;
    private final UserRoleRepo userRoleRepo;
    private final VendorAccess vendorAccess;

    @Override
    public void createVendor(VendorRequestDto request) {
        String slug = resolveSlug(request);

        if (vendorRepo.existsBySlug(slug)) {
            throw new GenericUncheckedException(VENDOR_SLUG_ALREADY_EXISTS_MESSAGE, VENDOR_SLUG_ALREADY_EXISTS, CONFLICT);
        }

        Vendor vendor = new Vendor();
        vendor.setName(request.getName().trim());
        vendor.setSlug(slug);
        vendor.setStatus(request.getStatus() != null ? request.getStatus() : VendorStatus.PENDING);
        vendor.setDescription(request.getDescription());
        vendor.setLogoUrl(request.getLogoUrl());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setIsActive(Boolean.TRUE);

        vendorRepo.save(vendor);
        log.info("Vendor created successfully with slug {}", slug);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponseDto getVendor(UUID uuid) {
        return VendorConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<VendorResponseDto> getPublicVendors() {
        return vendorRepo.findByIsActiveTrueAndStatusOrderByNameAsc(VendorStatus.APPROVED)
                .stream().map(VendorConverter::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<VendorResponseDto> getVendors(VendorSearchRequestDto request) {
        Pageable pageable = buildPageable(request);

        Page<VendorResponseDto> result = vendorRepo.searchVendors(
                normalizeSearchParam(request.getName()),
                normalizeSearchParam(request.getSlug()),
                request.getStatus(),
                pageable
        ).map(VendorConverter::toResponseDto);

        return PaginationMapper.toResponse(result);
    }

    @Override
    public void updateVendor(UUID uuid, VendorRequestDto request) {
        Vendor vendor = findByUuidOrThrow(uuid);
        String slug = resolveSlug(request);

        if (vendorRepo.existsBySlugAndIdNot(slug, vendor.getId())) {
            throw new GenericUncheckedException(VENDOR_SLUG_ALREADY_EXISTS_MESSAGE, VENDOR_SLUG_ALREADY_EXISTS, CONFLICT);
        }

        vendor.setName(request.getName().trim());
        vendor.setSlug(slug);

        if (request.getStatus() != null) {
            vendor.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            vendor.setDescription(request.getDescription());
        }
        if (request.getLogoUrl() != null) {
            vendor.setLogoUrl(request.getLogoUrl());
        }
        if (request.getContactEmail() != null) {
            vendor.setContactEmail(request.getContactEmail());
        }

        vendorRepo.save(vendor);
        log.info("Vendor updated successfully with uuid {}", uuid);
    }

    @Override
    public void deleteVendor(UUID uuid) {
        Vendor vendor = findByUuidOrThrow(uuid);
        vendor.setIsActive(Boolean.FALSE);
        vendorRepo.save(vendor);

        log.info("Vendor soft-deleted successfully with uuid {}", uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponseDto getVendorBySlug(String slug) {
        Vendor vendor = vendorRepo.findBySlug(slug)
                .orElseThrow(() -> new GenericUncheckedException(VENDOR_NOT_FOUND_MESSAGE + slug, VENDOR_NOT_FOUND, NOT_FOUND));
        return VendorConverter.toResponseDto(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponseDto getMyVendor() {
        User user = vendorAccess.currentUser();
        return vendorAccess.firstVendorOf(user)
                .map(VendorConverter::toResponseDto)
                .orElseThrow(() -> new GenericUncheckedException("No vendor found for current user", VENDOR_NOT_FOUND, NOT_FOUND));
    }

    /**
     * Platform-admin-only status change. On approval, activates the requester's
     * owner membership and grants a VENDOR-owner role - never platform rights.
     */
    @Override
    public void updateVendorStatus(UUID uuid, VendorStatus status) {
        User admin = vendorAccess.currentUser();
        if (!vendorAccess.isPlatformAdmin(admin)) {
            throw new GenericUncheckedException("Only platform administrators can change vendor status",
                    "VENDOR_STATUS_FORBIDDEN", FORBIDDEN);
        }
        Vendor vendor = findByUuidOrThrow(uuid);
        vendor.setStatus(status);
        vendorRepo.save(vendor);
        if (status == VendorStatus.APPROVED || status == VendorStatus.ACTIVE) {
            activateOwnerMembership(vendor);
        }
        log.info("Vendor {} status changed to {}", uuid, status);
    }

    private void activateOwnerMembership(Vendor vendor) {
        for (VendorMembership membership : membershipRepo.findByVendorIdAndStatus(vendor.getId(), MembershipStatus.PENDING)) {
            membership.setStatus(MembershipStatus.ACTIVE);
            membership.setJoinedAt(membership.getJoinedAt() != null ? membership.getJoinedAt() : java.time.LocalDateTime.now());
            membershipRepo.save(membership);
            grantVendorOwnerRole(membership.getUser(), vendor);
        }
    }

    private void grantVendorOwnerRole(User user, Vendor vendor) {
        Role ownerRole = roleRepo.findByCodeAndVendorId("VENDOR_OWNER", vendor.getId())
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("Vendor Owner");
                    role.setCode("VENDOR_OWNER");
                    role.setScope(RoleScope.VENDOR);
                    role.setVendor(vendor);
                    role.setIsSystemRole(Boolean.FALSE);
                    role.setIsActive(Boolean.TRUE);
                    return roleRepo.save(role);
                });
        if (!userRoleRepo.existsByUserRoleVendor(user.getId(), ownerRole.getId(), vendor)) {
            UserRole assignment = new UserRole();
            assignment.setUser(user);
            assignment.setRole(ownerRole);
            assignment.setVendor(vendor);
            assignment.setIsActive(Boolean.TRUE);
            userRoleRepo.save(assignment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Vendor getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    /**
     * Uses the supplied slug when present, otherwise derives a url-safe slug
     * from the vendor name.
     */
    private String resolveSlug(VendorRequestDto request) {
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = toSlug(request.getName());
        } else {
            slug = toSlug(slug);
        }

        if (slug.isBlank()) {
            throw new GenericUncheckedException(VENDOR_SLUG_REQUIRED_MESSAGE, VENDOR_SLUG_REQUIRED, BAD_REQUEST);
        }
        return slug;
    }

    private String toSlug(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
    }

    private String normalizeSearchParam(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    private Vendor findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(vendorRepo, uuid, VENDOR_NOT_FOUND_MESSAGE + uuid, VENDOR_NOT_FOUND);
    }

    private Pageable buildPageable(VendorSearchRequestDto request) {
        int page = request.getPage() == null ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null ? DEFAULT_SIZE : request.getSize();

        if (page < DEFAULT_PAGE) {
            throw new GenericUncheckedException(INVALID_PAGE_MESSAGE, INVALID_PAGE, BAD_REQUEST);
        }

        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new GenericUncheckedException(INVALID_PAGE_SIZE_MESSAGE, INVALID_PAGE_SIZE, BAD_REQUEST);
        }

        String sortBy = request.getSortBy() == null ? "createdAt" : request.getSortBy();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new GenericUncheckedException(
                    INVALID_SORT_FIELD_MESSAGE + sortBy,
                    INVALID_SORT_FIELD,
                    BAD_REQUEST
            );
        }

        String direction = request.getSortDirection() == null ? "DESC" : request.getSortDirection().toUpperCase(Locale.ROOT);

        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new GenericUncheckedException(INVALID_SORT_DIRECTION_MESSAGE, INVALID_SORT_DIRECTION, BAD_REQUEST);
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
    }
}

