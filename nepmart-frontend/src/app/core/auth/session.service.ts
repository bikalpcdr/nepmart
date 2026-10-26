import { Injectable, computed, inject, signal } from '@angular/core';
import { catchError, firstValueFrom, map, of, tap } from 'rxjs';
import type { Observable } from 'rxjs';
import { UserInitService } from '../user/user-init.service';
import type { UserInit } from '../../shared/models/user-init.model';

/**
 * Frontend copy of `GET /v1/users/me/init` (backend `UserInitService`).
 *
 * Loaded once after login and restored on reload when tokens exist
 * (see `APP_INITIALIZER` in `app.config.ts`). All helpers are UX hints
 * only — authorization is always enforced on the backend.
 */
@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly api = inject(UserInitService);

  private readonly _init = signal<UserInit | null>(null);
  readonly init = this._init.asReadonly();
  readonly user = computed(() => this._init()?.user ?? null);
  readonly roles = computed(() => this._init()?.roles ?? []);
  readonly vendors = computed(() => this._init()?.vendors ?? []);
  readonly permissions = computed(() => this._init()?.permissions ?? []);
  readonly permissionCodes = computed(
    () => this._init()?.permissionCodes ?? [],
  );
  readonly roleCodes = computed(() => this.roles().map((r) => r.code));
  readonly isLoaded = computed(() => this._init() !== null);

  /** Fetches the init payload and caches it. Errors propagate to the caller. */
  load(): Observable<UserInit> {
    return this.api.getInit().pipe(
      map((res) => res.data),
      tap((init) => this._init.set(init)),
    );
  }

  /** Best-effort restore for `APP_INITIALIZER` — never rejects. */
  restore(): Promise<UserInit | null> {
    return firstValueFrom(
      this.load().pipe(catchError(() => of(null))),
    ).catch(() => null);
  }

  clear(): void {
    this._init.set(null);
  }

  hasPermission(code: string): boolean {
    return this.permissionCodes().includes(code);
  }

  hasAnyPermission(codes: string[]): boolean {
    return codes.some((c) => this.permissionCodes().includes(c));
  }

  hasAllPermissions(codes: string[]): boolean {
    return codes.every((c) => this.permissionCodes().includes(c));
  }

  hasRole(code: string): boolean {
    return this.roleCodes().includes(code);
  }

  hasAnyRole(codes: string[]): boolean {
    return codes.some((c) => this.roleCodes().includes(c));
  }

  get isAdmin(): boolean {
    return this.user()?.userType === 'ADMIN' || this.hasRole('SUPER_ADMIN');
  }

  hasVendor(vendorUuid: string): boolean {
    return this.vendors().some((v) => v.uuid === vendorUuid);
  }
}
