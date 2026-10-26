package com.bikalpa.nepmart.core.user.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.user.dto.request.UserRequestDto;
import com.bikalpa.nepmart.core.user.dto.request.UserSearchRequestDto;
import com.bikalpa.nepmart.core.user.dto.response.UserInitResponseDto;
import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import com.bikalpa.nepmart.core.user.service.UserInitService;
import com.bikalpa.nepmart.core.user.service.UserService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserInitService userInitService;

    @PostMapping
    @SuccessMessage(action = CREATED, entity = USER)
    public void create(@Valid @RequestBody UserRequestDto request) {
        userService.createUser(request);
    }

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = USER)
    public BasePaginationResponseDto<UserResponseDto> getAll(@Valid @ModelAttribute UserSearchRequestDto request) {
        return userService.getUsers(request);
    }

    @GetMapping("/me/init")
    @PreAuthorize("isAuthenticated()")
    @SuccessMessage(action = FETCHED, entity = USER)
    public UserInitResponseDto getInit() {
        return userInitService.getCurrentUserInit();
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = USER)
    public UserResponseDto getById(@PathVariable UUID uuid) {
        return userService.getUser(uuid);
    }

    @PutMapping("/{uuid}")
    @SuccessMessage(action = UPDATED, entity = USER)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody UserRequestDto request) {
        userService.updateUser(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @SuccessMessage(action = DELETED, entity = USER)
    public void delete(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
    }
}
