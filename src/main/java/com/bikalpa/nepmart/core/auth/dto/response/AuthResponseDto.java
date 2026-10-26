package com.bikalpa.nepmart.core.auth.dto.response;

import com.bikalpa.nepmart.core.user.dto.response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Returned by register, login and refresh. Every field except {@code data} is
 * top-level so clients do not have to unwrap the ApiResponse envelope twice.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String accessToken;

    private String refreshToken;

    /** Always {@code Bearer} - tells the client how to send the access token. */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Access token lifetime in seconds, so clients can schedule a refresh. */
    private long expiresIn;

    private UserResponseDto user;
}
