package com.bikalpa.nepmart.core.permission.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.permission.dto.request.PermissionRequestDto;
import com.bikalpa.nepmart.core.permission.dto.request.PermissionSearchRequestDto;
import com.bikalpa.nepmart.core.permission.dto.response.PermissionResponseDto;
import com.bikalpa.nepmart.core.permission.service.PermissionService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @SuccessMessage(action = CREATED, entity = PERMISSION)
    public void create(@Valid @RequestBody PermissionRequestDto request) {
        permissionService.createPermission(request);
    }

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = PERMISSION)
    public BasePaginationResponseDto<PermissionResponseDto> getAll(@Valid @ModelAttribute PermissionSearchRequestDto request) {
        return permissionService.getPermissions(request);
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = PERMISSION)
    public PermissionResponseDto getById(@PathVariable UUID uuid) {
        return permissionService.getPermission(uuid);
    }

    @PutMapping("/{uuid}")
    @SuccessMessage(action = UPDATED, entity = PERMISSION)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody PermissionRequestDto request) {
        permissionService.updatePermission(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @SuccessMessage(action = DELETED, entity = PERMISSION)
    public void delete(@PathVariable UUID uuid) {
        permissionService.deletePermission(uuid);
    }
}
