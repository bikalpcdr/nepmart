package com.bikalpa.nepmart.core.user.dto.request;

import com.bikalpa.nepmart.core.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    /* Required when creating a user; optional for updates. */
    @Size(min = 8, max = 72)
    private String password;

    private UserStatus status;
}
