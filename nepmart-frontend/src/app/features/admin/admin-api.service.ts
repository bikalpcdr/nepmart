import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../../core/http/api.service';
import type {
  ApiResponse,
  ListParams,
  Page,
} from '../../shared/models/api-response.model';
import type {
  Permission,
  PermissionRequest,
  Role,
  RoleRequest,
  User,
  UserRole,
  UserRoleRequest,
  UserRequest,
  Vendor,
  VendorRequest,
} from '../../shared/models/user.model';

/**
 * Typed CRUD wrappers for the admin panel over the Nepmart backend
 * entities (`/v1/users`, `/v1/roles`, `/v1/permissions`, `/v1/vendors`,
 * `/v1/user-roles`).
 *
 * List methods unwrap `ApiResponse<Page<T>>` → `Page<T>`; mutations
 * return the full envelope so callers can toast `res.message` (§11.4).
 */
@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private readonly api = inject(ApiService);

  // ── Users ──────────────────────────────────────────────────────────
  listUsers(params?: ListParams): Observable<Page<User>> {
    return this.page<User>('/v1/users', params);
  }

  createUser(body: UserRequest): Observable<ApiResponse<void>> {
    return this.api.post<ApiResponse<void>>('/v1/users', body);
  }

  updateUser(
    uuid: string,
    body: UserRequest,
  ): Observable<ApiResponse<void>> {
    return this.api.put<ApiResponse<void>>(`/v1/users/${uuid}`, body);
  }

  deleteUser(uuid: string): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`/v1/users/${uuid}`);
  }

  // ── Roles ──────────────────────────────────────────────────────────
  listRoles(params?: ListParams): Observable<Page<Role>> {
    return this.page<Role>('/v1/roles', params);
  }

  createRole(body: RoleRequest): Observable<ApiResponse<void>> {
    return this.api.post<ApiResponse<void>>('/v1/roles', body);
  }

  updateRole(
    uuid: string,
    body: RoleRequest,
  ): Observable<ApiResponse<void>> {
    return this.api.put<ApiResponse<void>>(`/v1/roles/${uuid}`, body);
  }

  deleteRole(uuid: string): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`/v1/roles/${uuid}`);
  }

  // ── Permissions ────────────────────────────────────────────────────
  listPermissions(params?: ListParams): Observable<Page<Permission>> {
    return this.page<Permission>('/v1/permissions', params);
  }

  createPermission(body: PermissionRequest): Observable<ApiResponse<void>> {
    return this.api.post<ApiResponse<void>>('/v1/permissions', body);
  }

  updatePermission(
    uuid: string,
    body: PermissionRequest,
  ): Observable<ApiResponse<void>> {
    return this.api.put<ApiResponse<void>>(`/v1/permissions/${uuid}`, body);
  }

  deletePermission(uuid: string): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`/v1/permissions/${uuid}`);
  }

  // ── Vendors ────────────────────────────────────────────────────────
  listVendors(params?: ListParams): Observable<Page<Vendor>> {
    return this.page<Vendor>('/v1/vendors', params);
  }

  createVendor(body: VendorRequest): Observable<ApiResponse<void>> {
    return this.api.post<ApiResponse<void>>('/v1/vendors', body);
  }

  updateVendor(
    uuid: string,
    body: VendorRequest,
  ): Observable<ApiResponse<void>> {
    return this.api.put<ApiResponse<void>>(`/v1/vendors/${uuid}`, body);
  }

  deleteVendor(uuid: string): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`/v1/vendors/${uuid}`);
  }

  // ── User ↔ role assignments ────────────────────────────────────────
  listUserRoles(params?: ListParams): Observable<Page<UserRole>> {
    return this.page<UserRole>('/v1/user-roles', params);
  }

  assignUserRole(body: UserRoleRequest): Observable<ApiResponse<void>> {
    return this.api.post<ApiResponse<void>>('/v1/user-roles', body);
  }

  deleteUserRole(uuid: string): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`/v1/user-roles/${uuid}`);
  }

  private page<T>(path: string, params?: ListParams): Observable<Page<T>> {
    return this.api
      .get<ApiResponse<Page<T>>>(path, params)
      .pipe(map((res) => res.data));
  }
}
