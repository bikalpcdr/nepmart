import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiResponse } from '../../shared/models/api-response.model';
import type {
  AuthResponse,
  AuthTokens,
  LoginRequest,
  RefreshRequest,
  RegisterRequest,
} from '../../shared/models/auth.model';
import type { User } from '../../shared/models/user.model';
import { SessionService } from './session.service';
import { Router } from '@angular/router';

/**
 * How long (ms) before an access-token expiry we proactively refresh it.
 * The server issues a 15-minute access token, so we refresh at 60s before
 * expiry and refresh the token in a request that would otherwise 401.
 */
const REFRESH_AHEAD_MS = 60_000;
export { REFRESH_AHEAD_MS };

/**
 * Backend auth flow (§3):
 *   POST /v1/auth/login   -> returns full tokens + user
 *   POST /v1/auth/register-> returns full tokens + user (auto-authenticates)
 *   POST /v1/auth/refresh -> rotates the refresh token pair
 *   POST /v1/auth/logout  -> stateless no-op (client discards tokens)
 *
 * Responses are wrapped by ResponseWrapperAdvice into ApiResponse<AuthResponse>.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/v1/auth`;

  private readonly tokensSubject = new BehaviorSubject<AuthTokens | null>(this.readStoredTokens());
  readonly tokens$ = this.tokensSubject.asObservable();

  private readonly userSubject = new BehaviorSubject<User | null>(null);
  readonly user$ = this.userSubject.asObservable();

  isRefreshing = false;

  get accessToken(): string | null { return this.tokensSubject.value?.accessToken ?? null; }
  get isAuthenticated(): boolean { return this.accessToken !== null; }
  get expiresAt(): number | null { return this.tokensSubject.value?.expiresAt ?? null; }
  get refreshToken(): string | null { return this.tokensSubject.value?.refreshToken ?? null; }

  get isTokenExpired(): boolean {
    const exp = this.expiresAt;
    return exp !== null && Date.now() >= exp;
  }

  get needsRefresh(): boolean {
    const exp = this.expiresAt;
    if (exp === null) return true;
    return Date.now() >= exp - REFRESH_AHEAD_MS;
  }

  login(body: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, body)
      .pipe(tap((res) => this.onAuthResponse(res.data)));
  }

  register(body: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, body)
      .pipe(tap((res) => this.onAuthResponse(res.data)));
  }

  refresh(): Observable<ApiResponse<AuthResponse>> {
    const refreshToken = this.tokensSubject.value?.refreshToken;
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/refresh`, { refreshToken })
      .pipe(tap((res) => this.storeTokens(res.data)));
  }

  logout(): void {
    // Server-side logout is a stateless no-op; nothing is revoked server-side.
    this.http.post(`${this.baseUrl}/logout`, {}).subscribe({ next: () => {}, error: () => {} });
    this.clearSession();
  }

  signOut(): void {
    this.clearSession();
    this.router.navigateByUrl('/login');
  }

  private onAuthResponse(response: AuthResponse): void {
    this.storeTokens(response);
    // Warm the RBAC session in the background. Errors are ignored: the user is
    // still authenticated even if the session payload can't be fetched.
    void this.session.load().pipe(tap((init) => this.userSubject.next(init.user))).subscribe();
  }

  private storeTokens(response: AuthResponse): void {
    const expiresAt = Date.now() + response.expiresIn * 1000;
    const session: AuthTokens = {
      ...response,
      expiresAt,
    };
    this.tokensSubject.next(session);
    localStorage.setItem('nepmart_tokens', JSON.stringify(session));
  }

  private readStoredTokens(): AuthTokens | null {
    try {
      const raw = localStorage.getItem('nepmart_tokens');
      if (!raw) return null;
      const parsed = JSON.parse(raw) as AuthTokens;
      if (!parsed?.accessToken) return null;
      return parsed;
    } catch {
      return null;
    }
  }

  private clearSession(): void {
    this.tokensSubject.next(null);
    this.userSubject.next(null);
    this.session.clear();
    localStorage.removeItem('nepmart_tokens');
  }

  private tokensSnapshot(res: ApiResponse<AuthResponse>): Observable<ApiResponse<AuthResponse>> {
    return new Observable<ApiResponse<AuthResponse>>((subscriber) => {
      subscriber.next(res);
      subscriber.complete();
    });
  }
}
