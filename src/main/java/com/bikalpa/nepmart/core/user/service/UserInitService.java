package com.bikalpa.nepmart.core.user.service;

import com.bikalpa.nepmart.core.user.dto.response.UserInitResponseDto;

/**
 * Provides the authenticated user's initialization payload.
 *
 * <p>Backs {@code GET /v1/users/me/init} (served under the {@code /api}
 * context-path). The payload aggregates public user details, assigned active
 * roles, vendor context and distinct active permissions so the frontend can
 * bootstrap RBAC without extra round-trips.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 */
public interface UserInitService {

    /**
     * Loads init data for the currently authenticated user.
     *
     * @return user details + active roles + vendor context + distinct active permissions
     */
    UserInitResponseDto getCurrentUserInit();
}
