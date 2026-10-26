import {
  APP_INITIALIZER,
  ApplicationConfig,
  inject,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { routes } from './app.routes';
import { environment } from '../environments/environment';
import { provideCore } from './core/core.config';
import { AuthService } from './core/auth/auth.service';
import { SessionService } from './core/auth/session.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideCore(),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
      }),
    ),
    {
      provide: 'API_URL',
      useValue: environment.apiUrl,
    },
    {
      provide: APP_INITIALIZER,
      multi: true,
      // Restores `GET /v1/users/me/init` when tokens survived a reload.
      useFactory: () => {
        const auth = inject(AuthService);
        const session = inject(SessionService);
        return () =>
          auth.isAuthenticated ? session.restore().then(() => undefined) : undefined;
      },
    },
  ],
};
