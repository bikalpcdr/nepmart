package com.bikalpa.nepmart.core.user.service;

import com.bikalpa.nepmart.core.user.dto.request.UserRequestDto;
import com.bikalpa.nepmart.core.user.dto.request.UserSearchRequestDto;
import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
public interface UserService {
    void createUser(UserRequestDto request);

    UserResponseDto getUser(UUID uuid);

    BasePaginationResponseDto<UserResponseDto> getUsers(UserSearchRequestDto request);

    void updateUser(UUID uuid, UserRequestDto request);

    void deleteUser(UUID uuid);

    User getEntity(UUID uuid);
}
