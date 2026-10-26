package com.bikalpa.nepmart.core.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Public self-registration payload. Deliberately narrower than
 * {@code UserRequestDto} - a signup must never be able to set {@code status}.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    /* 72 is BCrypt's hard limit; longer inputs are rejected rather than truncated. */
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;
}
