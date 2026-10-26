package com.bikalpa.nepmart.core.role.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.role.dto.request.UserRoleRequestDto;
import com.bikalpa.nepmart.core.role.dto.request.UserRoleSearchRequestDto;
import com.bikalpa.nepmart.core.role.dto.response.UserRoleResponseDto;
import com.bikalpa.nepmart.core.role.service.UserRoleService;
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
@RequestMapping("/v1/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @SuccessMessage(action = CREATED, entity = ROLE)
    public UserRoleResponseDto assign(@Valid @RequestBody UserRoleRequestDto request) {
        return userRoleService.assignRole(request);
    }

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = ROLE)
    public BasePaginationResponseDto<UserRoleResponseDto> getAll(@Valid @ModelAttribute UserRoleSearchRequestDto request) {
        return userRoleService.getUserRoles(request);
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = ROLE)
    public UserRoleResponseDto getById(@PathVariable UUID uuid) {
        return userRoleService.getUserRole(uuid);
    }

    @DeleteMapping("/{uuid}")
    @SuccessMessage(action = DELETED, entity = ROLE)
    public void delete(@PathVariable UUID uuid) {
        userRoleService.removeUserRole(uuid);
    }
}
