import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../http/api.service';
import type { ApiResponse } from '../../shared/models/api-response.model';
import type { UserInit } from '../../shared/models/user-init.model';

/**
 * Calls `GET /v1/users/me/init` for the authenticated user.
 * The `Authorization: Bearer` header is attached by `authInterceptor`;
 * `/v1/auth/**` stays public, everything else requires a token.
 */
@Injectable({ providedIn: 'root' })
export class UserInitService {
  private readonly api = inject(ApiService);

  getInit(): Observable<ApiResponse<UserInit>> {
    return this.api.get<ApiResponse<UserInit>>('/v1/users/me/init');
  }
}
