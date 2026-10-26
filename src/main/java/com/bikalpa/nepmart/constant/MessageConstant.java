package com.bikalpa.nepmart.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstant {
    /* Generic success template */
    public static final String SUCCESS_TEMPLATE = "%s %s successfully!";

    /*  Authentication */
    public static final String LOGIN = "login";
    public static final String REGISTER = "registered";
    public static final String LOGOUT = "logout";

    /* CRUD actions*/
    public static final String CREATED = "created";
    public static final String FETCHED = "fetched";
    public static final String UPDATED = "updated";
    public static final String DELETED = "deleted";

    /* CRUD success messages */
    public static final String CREATE_SUCCESS = "created successfully!";
    public static final String FETCH_SUCCESS = "fetched successfully!";
    public static final String UPDATE_SUCCESS = "updated successfully!";
    public static final String DELETE_SUCCESS = "deleted successfully!";

    /* Common operation messages*/
    public static final String OPERATION_SUCCESS = "Operation completed successfully!";
    public static final String STATUS_UPDATED = "status updated successfully!";
    public static final String ACTIVATED = "activated successfully!";
    public static final String DEACTIVATED = "deactivated successfully!";

    /* resources*/
    public static final String USER = "User";
    public static final String VENDOR = "Vendor";
    public static final String ROLE = "Role";
    public static final String PERMISSION = "Permission";
    public static final String CATEGORY = "Category";
    public static final String PRODUCT = "Product";
    public static final String BANNER = "Banner";
    public static final String CART = "Cart";
    public static final String ORDER = "Order";
    public static final String NOTIFICATION = "Notification";
    public static final String SUBSCRIPTION = "Push subscription";

    /* User Error Codes */
    public static final String PASSWORD_REQUIRED = "PASSWORD_REQUIRED";
    public static final String EMAIL_REQUIRED = "EMAIL_REQUIRED";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String INVALID_PAGE = "INVALID_PAGE";
    public static final String INVALID_PAGE_SIZE = "INVALID_PAGE_SIZE";
    public static final String INVALID_SORT_FIELD = "INVALID_SORT_FIELD";
    public static final String INVALID_SORT_DIRECTION = "INVALID_SORT_DIRECTION";

    /* User Error Messages */
    public static final String PASSWORD_REQUIRED_MESSAGE = "Password is required";
    public static final String EMAIL_REQUIRED_MESSAGE = "Email is required";
    public static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email already exists";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found with uuid: ";
    public static final String INVALID_PAGE_MESSAGE = "Page must be zero or greater";
    public static final String INVALID_PAGE_SIZE_MESSAGE = "Page size must be between 1 and 100";
    public static final String INVALID_SORT_FIELD_MESSAGE = "Unsupported sort field: ";
    public static final String INVALID_SORT_DIRECTION_MESSAGE = "Sort direction must be ASC or DESC";

    /* Authentication actions */
    public static final String LOGGED_IN = "logged in";
    public static final String LOGGED_OUT = "logged out";
    public static final String REFRESHED = "refreshed";

    /* Auth Error Codes */
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ACCOUNT_NOT_ACTIVE = "ACCOUNT_NOT_ACTIVE";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String REFRESH_TOKEN_REQUIRED = "REFRESH_TOKEN_REQUIRED";

    /* Auth Error Messages */
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";
    public static final String ACCOUNT_NOT_ACTIVE_MESSAGE = "Account is not active. Please contact support.";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid or malformed token";
    public static final String TOKEN_EXPIRED_MESSAGE = "Token has expired. Please login again.";
    public static final String REFRESH_TOKEN_REQUIRED_MESSAGE = "Refresh token is required";
    public static final String AUTHENTICATION_REQUIRED_MESSAGE = "Authentication required";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to perform this action";

    /* Vendor Error Codes */
    public static final String VENDOR_NOT_FOUND = "VENDOR_NOT_FOUND";
    public static final String VENDOR_SLUG_REQUIRED = "VENDOR_SLUG_REQUIRED";
    public static final String VENDOR_SLUG_ALREADY_EXISTS = "VENDOR_SLUG_ALREADY_EXISTS";

    /* Vendor Error Messages */
    public static final String VENDOR_NOT_FOUND_MESSAGE = "Vendor not found with uuid: ";
    public static final String VENDOR_SLUG_REQUIRED_MESSAGE = "Slug is required";
    public static final String VENDOR_SLUG_ALREADY_EXISTS_MESSAGE = "Slug already exists";

    /* Role Error Codes */
    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
    public static final String ROLE_CODE_REQUIRED = "ROLE_CODE_REQUIRED";
    public static final String ROLE_CODE_ALREADY_EXISTS = "ROLE_CODE_ALREADY_EXISTS";
    public static final String ROLE_SCOPE_REQUIRED = "ROLE_SCOPE_REQUIRED";
    public static final String ROLE_VENDOR_REQUIRED = "ROLE_VENDOR_REQUIRED";
    public static final String ROLE_VENDOR_NOT_ALLOWED = "ROLE_VENDOR_NOT_ALLOWED";
    public static final String SYSTEM_ROLE_IMMUTABLE = "SYSTEM_ROLE_IMMUTABLE";

    /* Role Error Messages */
    public static final String ROLE_NOT_FOUND_MESSAGE = "Role not found with uuid: ";
    public static final String ROLE_CODE_REQUIRED_MESSAGE = "Role code is required";
    public static final String ROLE_CODE_ALREADY_EXISTS_MESSAGE = "Role code already exists";
    public static final String ROLE_SCOPE_REQUIRED_MESSAGE = "Role scope is required";
    public static final String ROLE_VENDOR_REQUIRED_MESSAGE = "A vendor is required for VENDOR-scoped roles";
    public static final String ROLE_VENDOR_NOT_ALLOWED_MESSAGE = "A SYSTEM-scoped role must not reference a vendor";
    public static final String SYSTEM_ROLE_IMMUTABLE_MESSAGE = "System roles cannot be modified or deleted";

    /* UserRole Error Codes */
    public static final String USER_ROLE_NOT_FOUND = "USER_ROLE_NOT_FOUND";
    public static final String USER_ROLE_ALREADY_ASSIGNED = "USER_ROLE_ALREADY_ASSIGNED";

    /* UserRole Error Messages */
    public static final String USER_ROLE_NOT_FOUND_MESSAGE = "User role assignment not found with uuid: ";
    public static final String USER_ROLE_ALREADY_ASSIGNED_MESSAGE = "This role is already assigned to the user for the same scope";

    /* Permission Error Codes */
    public static final String PERMISSION_NOT_FOUND = "PERMISSION_NOT_FOUND";
    public static final String PERMISSION_CODE_REQUIRED = "PERMISSION_CODE_REQUIRED";
    public static final String PERMISSION_CODE_ALREADY_EXISTS = "PERMISSION_CODE_ALREADY_EXISTS";

    /* Permission Error Messages */
    public static final String PERMISSION_NOT_FOUND_MESSAGE = "Permission not found with uuid: ";
    public static final String PERMISSION_CODE_REQUIRED_MESSAGE = "Permission code is required";
    public static final String PERMISSION_CODE_ALREADY_EXISTS_MESSAGE = "Permission code already exists";

    /* RolePermission Error Codes */
    public static final String ROLE_PERMISSION_NOT_FOUND = "ROLE_PERMISSION_NOT_FOUND";
    public static final String ROLE_PERMISSION_ALREADY_ASSIGNED = "ROLE_PERMISSION_ALREADY_ASSIGNED";

    /* RolePermission Error Messages */
    public static final String ROLE_PERMISSION_NOT_FOUND_MESSAGE = "Permission is not assigned to this role";
    public static final String ROLE_PERMISSION_ALREADY_ASSIGNED_MESSAGE = "This permission is already assigned to the role";
}
