import type { ApiResponse } from './api-response.model';
import type { Permission, Role, User, Vendor } from './user.model';

/**
 * Aggregated init payload — mirrors backend `UserInitResponseDto`
 * served by `GET /v1/users/me/init` (see `UserInitService`).
 * Public data only: never passwords or secrets.
 */
export interface UserInit {
  user: User;
  roles: Role[];
  vendors: Vendor[];
  permissions: Permission[];
  /** Distinct permission codes (e.g. `user:read`) for quick UI checks. */
  permissionCodes: string[];
}

export type UserInitResponse = ApiResponse<UserInit>;
