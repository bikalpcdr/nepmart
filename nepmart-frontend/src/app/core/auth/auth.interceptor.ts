import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

/**
 * Attaches `Authorization: Bearer <accessToken>` to backend calls (§11.3).
 * Public auth endpoints (/v1/auth/**) are left untouched.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.accessToken;

  if (!token || req.url.includes('/v1/auth/')) {
    return next(req);
  }

  return next(
    req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }),
  );
};
