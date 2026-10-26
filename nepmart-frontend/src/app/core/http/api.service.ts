import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpParams,
  type HttpParamsOptions,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ListParams } from '../../shared/models/api-response.model';

/**
 * Thin wrapper over HttpClient that prefixes `environment.apiUrl`
 * and serializes list params (page/size/sortBy/sortDirection — §9).
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  readonly baseUrl = environment.apiUrl;

  get<T>(path: string, params?: ListParams): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, {
      params: this.toParams(params),
    });
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  put<T>(path: string, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body);
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`);
  }

  private toParams(params?: ListParams) {
    if (!params) return undefined;
    const init: HttpParamsOptions['fromObject'] = {};
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null && value !== '') {
        init[key] = String(value);
      }
    }
    return new HttpParams({ fromObject: init });
  }
}
