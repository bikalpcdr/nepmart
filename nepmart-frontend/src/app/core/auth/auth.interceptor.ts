import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { EMPTY, catchError, switchMap, take, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Auth interceptor.
 *
 *  1. Never attaches a token to public endpoints (/v1/auth/** and the public
 *     catalog/swagger/actuator paths that SecurityConfig permits).
 *
 *  2. Proactively refreshes an access token that is about to expire before
 *     the next request is sent, so a 401 due to expiry never reaches the
 *     caller.
 *
 *  3. On a 401 belongs to a protected endpoint, rotates via /refresh and
 *     retries the *request once* with the new token. If the refresh token is
 *     also invalid/expired the user is signed out and redirected to /login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  // Public endpoints are never token-protected (see SecurityConfig).
  if (req.url.includes('/v1/auth/')) {
    return next(req);
  }
  if (isPublicPath(req.url)) {
    return next(req);
  }

  const token = auth.accessToken;
  if (!token) {
    return next(req);
  }

  if (auth.needsRefresh && !auth.isRefreshing) {
    auth.isRefreshing = true;
    void auth
      .refresh()
      .pipe(take(1))
      .subscribe({
        next: () => {
          auth.isRefreshing = false;
        },
        error: () => {
          auth.signOut();
        },
      });
    return EMPTY;
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401) {
        // Already in the middle of a refresh: do not start a second one.
        if (auth.isRefreshing) {
          return throwError(() => err);
        }
        const refreshToken = auth.refreshToken;
        if (!refreshToken) {
          auth.signOut();
          return throwError(() => err);
        }
        auth.isRefreshing = true;
        return auth
          .refresh()
          .pipe(
            switchMap(() => next(req.clone({ setHeaders: { Authorization: `Bearer ${auth.accessToken}` } }))),
            catchError((refreshErr: unknown) => {
              auth.isRefreshing = false;
              auth.signOut();
              return throwError(() => refreshErr);
            }),
          );
      }
      return throwError(() => err);
    }),
  );
};

function isPublicPath(url: string): boolean {
  return [
    '/v3/api-docs',
    '/swagger-ui',
    '/api-docs',
    '/actuator/health',
    '/v1/homepage',
    '/v1/categories',
    '/v1/products',
    '/v1/banners/live',
    '/v1/vendors',
  ].some((prefix) => url.startsWith(prefix));
}
