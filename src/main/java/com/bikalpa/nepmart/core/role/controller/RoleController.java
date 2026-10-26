package com.bikalpa.nepmart.core.role.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.role.dto.request.RoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.RoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.RoleResponseDto;
import com.bikalpa.nepmart.core.role.service.RoleService;
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
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @SuccessMessage(action = CREATED, entity = ROLE)
    public void create(@Valid @RequestBody RoleRequestDto request) {
        roleService.createRole(request);
    }

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = ROLE)
    public BasePaginationResponseDto<RoleResponseDto> getAll(@Valid @ModelAttribute RoleSearchRequestDto request) {
        return roleService.getRoles(request);
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = ROLE)
    public RoleResponseDto getById(@PathVariable UUID uuid) {
        return roleService.getRole(uuid);
    }

    @PutMapping("/{uuid}")
    @SuccessMessage(action = UPDATED, entity = ROLE)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody RoleRequestDto request) {
        roleService.updateRole(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @SuccessMessage(action = DELETED, entity = ROLE)
    public void delete(@PathVariable UUID uuid) {
        roleService.deleteRole(uuid);
    }
}
