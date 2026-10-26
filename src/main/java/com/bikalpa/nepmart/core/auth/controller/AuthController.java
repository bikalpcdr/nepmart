package com.bikalpa.nepmart.core.auth.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.auth.dto.request.LoginRequestDto;
import com.bikalpa.nepmart.core.auth.dto.request.RefreshTokenRequestDto;
import com.bikalpa.nepmart.core.auth.dto.request.RegisterRequestDto;
import com.bikalpa.nepmart.core.auth.dto.response.AuthResponseDto;
import com.bikalpa.nepmart.core.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

/**
 * Authentication endpoints, all publicly reachable via the
 * {@code /v1/auth/**} rule in {@code SecurityConfig}.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @SuccessMessage(action = REGISTER, entity = USER)
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @SuccessMessage(action = LOGIN, entity = USER)
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    /**
     * Exchanges a refresh token for a new token pair, rotating the refresh token.
     */
    @PostMapping("/refresh")
    @SuccessMessage(action = REFRESHED, entity = USER)
    public AuthResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return authService.refresh(request);
    }

    /**
     * Stateless logout: the client just discards its tokens.
     *
     * <p>Kept as a real endpoint rather than omitted so clients have somewhere to
     * post to, and so a server-side denylist can be added later without a breaking
     * change. Tokens stay valid until they expire - to cut that window short, add
     * a revocation list checked in {@code JwtAuthenticationFilter}.
     */
    @PostMapping("/logout")
    @SuccessMessage(action = LOGOUT, entity = USER)
    public void logout() {
        /* Nothing to do server side; see javadoc. */
    }
}
