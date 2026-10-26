package com.bikalpa.nepmart.core.user.service.impl;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.user.utility.UserConverter;
import com.bikalpa.nepmart.core.user.dto.request.UserRequestDto;
import com.bikalpa.nepmart.core.user.dto.request.UserSearchRequestDto;
import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import com.bikalpa.nepmart.core.user.service.UserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "email", "status", "createdAt", "updatedAt"
    );

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserRequestDto request) {
        String email = normalizeEmail(request.getEmail());

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new GenericUncheckedException(PASSWORD_REQUIRED_MESSAGE, PASSWORD_REQUIRED, BAD_REQUEST);
        }

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new GenericUncheckedException(EMAIL_ALREADY_EXISTS_MESSAGE, EMAIL_ALREADY_EXISTS, CONFLICT
            );
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null ? request.getStatus() : UserStatus.PENDING_VERIFICATION);
        user.setIsActive(Boolean.TRUE);

        userRepo.save(user);
        log.info("User created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID uuid) {
        return UserConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<UserResponseDto> getUsers(UserSearchRequestDto request) {
        Pageable pageable = buildPageable(request);

        // '' (not null) = "no filter": keeps the JPQL text parameter typed on
        // PostgreSQL, where a null String bind inside lower()/concat() fails
        // with "function lower(bytea) does not exist".
        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            email = "";
        } else {
            email = email.trim();
        }

        Page<UserResponseDto> result = userRepo.searchUsers(
                email,
                request.getStatus(),
                request.getIsActive(),
                pageable
        ).map(UserConverter::toResponseDto);

        return PaginationMapper.toResponse(result);
    }

    @Override
    public void updateUser(UUID uuid, UserRequestDto request) {
        User user = findByUuidOrThrow(uuid);
        String email = normalizeEmail(request.getEmail());

        if (userRepo.existsByEmailIgnoreCaseAndIdNot(email, user.getId())) {
            throw new GenericUncheckedException(EMAIL_ALREADY_EXISTS_MESSAGE, EMAIL_ALREADY_EXISTS, CONFLICT);
        }

        user.setEmail(email);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userRepo.save(user);
        log.info("User updated successfully");
    }

    @Override
    public void deleteUser(UUID uuid) {
        User user = findByUuidOrThrow(uuid);
        user.setIsActive(Boolean.FALSE);
        userRepo.save(user);

        log.info("User soft-deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public User getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    private User findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(userRepo, uuid, USER_NOT_FOUND_MESSAGE + uuid, USER_NOT_FOUND);
    }

    private Pageable buildPageable(UserSearchRequestDto request) {
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

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new GenericUncheckedException(EMAIL_REQUIRED_MESSAGE, EMAIL_REQUIRED, BAD_REQUEST);
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}