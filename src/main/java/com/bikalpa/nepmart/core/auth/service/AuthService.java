package com.bikalpa.nepmart.core.auth.service;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.auth.dto.request.LoginRequestDto;
import com.bikalpa.nepmart.core.auth.dto.request.RefreshTokenRequestDto;
import com.bikalpa.nepmart.core.auth.dto.request.RegisterRequestDto;
import com.bikalpa.nepmart.core.auth.dto.response.AuthResponseDto;
import com.bikalpa.nepmart.core.auth.utility.JwtUtility;
import com.bikalpa.nepmart.core.user.dto.request.UserRequestDto;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import com.bikalpa.nepmart.core.user.service.UserService;
import com.bikalpa.nepmart.core.user.utility.UserConverter;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Register / login / refresh-token flows.
 *
 * <p>Logout is intentionally absent: with stateless JWTs there is no session to
 * destroy, so discarding the tokens client side is the entire operation. That is
 * why {@code AuthController#logout} only documents the behaviour.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtility jwtUtility;

    /**
     * Creates a pending-verification account and returns a token pair.
     *
     * <p>Creation is delegated to {@link UserService#createUser} so email
     * normalization and the duplicate-email check live in exactly one place.
     */
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        UserRequestDto createRequest = new UserRequestDto();
        createRequest.setEmail(request.getEmail());
        createRequest.setPassword(request.getPassword());
        createRequest.setStatus(UserStatus.PENDING_VERIFICATION);

        userService.createUser(createRequest);

        User user = userRepo.findByEmailIgnoreCase(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new GenericUncheckedException(
                        MessageConstant.USER_NOT_FOUND_MESSAGE + request.getEmail(),
                        MessageConstant.USER_NOT_FOUND,
                        UNAUTHORIZED));

        return buildAuthResponse(user);
    }

    /**
     * Authenticates by delegating to the {@link AuthenticationManager}, which
     * checks the password hash together with the disabled/locked flags set by
     * {@code AppUserDetailsService}.
     *
     * <p>A wrong password and an unknown email both surface as the same message,
     * so the endpoint cannot be used to enumerate accounts.
     */
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            if (authentication == null || !authentication.isAuthenticated()) {
                throw invalidCredentials();
            }
        } catch (AuthenticationException ex) {
            throw invalidCredentials();
        }

        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(AuthService::invalidCredentials);

        return buildAuthResponse(user);
    }

    /**
     * Exchanges a valid refresh token for a new token pair.
     *
     * <p>Rotates the refresh token on every call so a leaked token has only a
     * single-use window. The superseded token still validates until it expires -
     * real revocation would need a server-side denylist.
     */
    @Transactional(readOnly = true)
    public AuthResponseDto refresh(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtility.isRefreshToken(refreshToken)) {
            throw invalidRefreshToken();
        }

        String email = jwtUtility.extractEmail(refreshToken);

        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> invalidRefreshToken());

        return buildAuthResponse(user);
    }

    private static GenericUncheckedException invalidCredentials() {
        return new GenericUncheckedException(
                MessageConstant.INVALID_CREDENTIALS_MESSAGE,
                MessageConstant.INVALID_CREDENTIALS,
                UNAUTHORIZED);
    }

    private static GenericUncheckedException invalidRefreshToken() {
        return new GenericUncheckedException(
                MessageConstant.REFRESH_TOKEN_REQUIRED_MESSAGE,
                MessageConstant.REFRESH_TOKEN_REQUIRED,
                UNAUTHORIZED);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        return AuthResponseDto.builder()
                .accessToken(jwtUtility.generateAccessToken(user))
                .refreshToken(jwtUtility.generateRefreshToken(user))
                .expiresIn(jwtUtility.accessTokenExpirationSeconds())
                .user(UserConverter.toResponseDto(user))
                .build();
    }
}
