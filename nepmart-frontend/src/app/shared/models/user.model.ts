/**
 * Domain models — mirrors `UserInitService` response DTOs:
 * `UserResponseDto`, `RoleResponseDto`, `VendorResponseDto`,
 * `PermissionResponseDto` (+ `BaseDto` uuid/isActive).
 */

export type UserStatus =
  | 'ACTIVE'
  | 'INACTIVE'
  | 'BLOCKED'
  | 'PENDING_VERIFICATION';
export type UserType = 'CUSTOMER' | 'VENDOR' | 'ADMIN';
export type VendorStatus =
  | 'PENDING'
  | 'ACTIVE'
  | 'SUSPENDED'
  | 'REJECTED'
  | 'INACTIVE';
export type RoleScope = 'SYSTEM' | 'VENDOR';

export interface User {
  uuid: string;
  isActive: boolean;
  email: string;
  status: UserStatus;
  userType: UserType;
}

export interface UserRequest {
  /* Required when creating a user; optional on update (omit to keep). */
  email: string;
  password?: string;
  status?: UserStatus;
}

export interface Vendor {
  uuid: string;
  isActive: boolean;
  name: string;
  slug: string;
  status: VendorStatus;
}

export interface VendorRequest {
  name: string;
  slug?: string;
  status?: VendorStatus;
}

export interface Role {
  uuid: string;
  isActive: boolean;
  name: string;
  code: string;
  scope: RoleScope;
  isSystemRole: boolean;
  vendorUuid: string | null;
  vendorName: string | null;
}

export interface RoleRequest {
  name: string;
  code: string;
  scope: RoleScope;
  vendorUuid?: string;
}

export interface UserRole {
  uuid: string;
  isActive: boolean;
  userUuid: string;
  userEmail: string;
  roleUuid: string;
  roleCode: string;
  roleName: string;
  vendorUuid: string | null;
  vendorName: string | null;
}

export interface UserRoleRequest {
  userUuid: string;
  roleUuid: string;
  vendorUuid?: string;
}

export interface Permission {
  uuid: string;
  isActive: boolean;
  name: string;
  code: string;
  module: string;
  action: string;
}

export interface PermissionRequest {
  name: string;
  code: string;
  module: string;
  action: string;
}
