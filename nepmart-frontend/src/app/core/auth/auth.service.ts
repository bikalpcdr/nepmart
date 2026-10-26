import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiResponse } from '../../shared/models/api-response.model';
import type {
  AuthTokens,
  LoginRequest,
  RefreshRequest,
  RegisterRequest,
} from '../../shared/models/auth.model';
import type { User } from '../../shared/models/user.model';
import { SessionService } from './session.service';

/**
 * Auth state + API calls (docs/API_INTEGRATION_GUIDE.md §3).
 * Tokens are kept in memory + localStorage; refresh proactively ~60s
 * before `expiresIn` (see §11.5).
 *
 * After login/register the `GET /v1/users/me/init` payload is warmed into
 * `SessionService` so RBAC data (roles/vendors/permissions) is ready.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = inject(SessionService);
  private readonly baseUrl = `${environment.apiUrl}/v1/auth`;

  private readonly tokensSubject = new BehaviorSubject<AuthTokens | null>(
    this.readStoredTokens(),
  );
  readonly tokens$ = this.tokensSubject.asObservable();

  private readonly userSubject = new BehaviorSubject<User | null>(null);
  readonly user$ = this.userSubject.asObservable();

  get accessToken(): string | null {
    return this.tokensSubject.value?.accessToken ?? null;
  }

  get isAuthenticated(): boolean {
    return this.accessToken !== null;
  }

  login(body: LoginRequest): Observable<ApiResponse<AuthTokens>> {
    return this.http
      .post<ApiResponse<AuthTokens>>(`${this.baseUrl}/login`, body)
      .pipe(
        tap((res) => this.storeTokens(res.data)),
        switchMap((res) =>
          this.session.load().pipe(
            tap((init) => this.userSubject.next(init.user)),
            // Re-emit the original login response so callers keep their contract.
            switchMap(() => this.tokensSnapshot(res)),
          ),
        ),
      );
  }

  register(body: RegisterRequest): Observable<ApiResponse<unknown>> {
    return this.http.post<ApiResponse<unknown>>(
      `${this.baseUrl}/register`,
      body,
    );
  }

  refresh(): Observable<ApiResponse<AuthTokens>> {
    const refreshToken = this.tokensSubject.value?.refreshToken;
    const body: RefreshRequest = { refreshToken: refreshToken ?? '' };
    return this.http
      .post<ApiResponse<AuthTokens>>(`${this.baseUrl}/refresh`, body)
      .pipe(tap((res) => this.storeTokens(res.data)));
  }

  /** Logout is client-side only (backend has no denylist yet — see §12). */
  logout(): void {
    this.tokensSubject.next(null);
    this.userSubject.next(null);
    this.session.clear();
    localStorage.removeItem('nepmart_tokens');
  }

  /** Re-emits the original login response after the session chain. */
  private tokensSnapshot(
    res: ApiResponse<AuthTokens>,
  ): Observable<ApiResponse<AuthTokens>> {
    return new Observable((subscriber) => {
      subscriber.next(res);
      subscriber.complete();
    });
  }

  private storeTokens(tokens: AuthTokens): void {
    this.tokensSubject.next(tokens);
    localStorage.setItem('nepmart_tokens', JSON.stringify(tokens));
  }

  private readStoredTokens(): AuthTokens | null {
    try {
      const raw = localStorage.getItem('nepmart_tokens');
      return raw ? (JSON.parse(raw) as AuthTokens) : null;
    } catch {
      return null;
    }
  }
}
