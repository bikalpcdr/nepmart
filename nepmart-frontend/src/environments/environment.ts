/**
 * Environment configuration for the Nepmart Frontend (Angular 22).
 *
 * Ref: docs/API_INTEGRATION_GUIDE.md §11 (Angular integration checklist)
 *      - Base URL: http://localhost:7777/api
 *      - CORS is already wide open (*), so a dev proxy is optional.
 */
export const environment = {
  production: false,
  apiUrl: 'http://localhost:7777/api',
};
