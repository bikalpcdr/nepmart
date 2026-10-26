import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { SessionService } from './session.service';

/**
 * Route guards over the init payload (`GET /v1/users/me/init`).
 * Frontend hints only — every backend endpoint still enforces RBAC itself.
 *
 * Usage: `canActivate: [requirePermissions('user:read')]`
 */
export function requirePermissions(...codes: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const session = inject(SessionService);
    const router = inject(Router);
    if (!auth.isAuthenticated) return router.createUrlTree(['/login']);
    if (!session.hasAnyPermission(codes)) return router.createUrlTree(['/']);
    return true;
  };
}

/** Usage: `canActivate: [requireRoles('SUPER_ADMIN')]` */
export function requireRoles(...codes: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const session = inject(SessionService);
    const router = inject(Router);
    if (!auth.isAuthenticated) return router.createUrlTree(['/login']);
    if (!session.hasAnyRole(codes)) return router.createUrlTree(['/']);
    return true;
  };
}
