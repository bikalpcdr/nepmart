import { provideHttpClient, withInterceptors } from '@angular/common/http';
import type { EnvironmentProviders } from '@angular/core';
import { authInterceptor } from './auth/auth.interceptor';

/** Core providers (HttpClient + auth interceptor) for app.config.ts. */
export function provideCore(): EnvironmentProviders {
  return provideHttpClient(withInterceptors([authInterceptor]));
}
