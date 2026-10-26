import type { User } from './user.model';

/** Auth payloads — mirrors docs/API_INTEGRATION_GUIDE.md §3. */
/** Server-side auth envelope returned by /v1/auth/* endpoints. */
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

/** Client-side token snapshot. `expiresAt` is derived server-side is not sent. */
export interface AuthTokens extends AuthResponse {
  expiresAt: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

/** Decoded access-token claims (§3.4). */
export interface JwtClaims {
  sub: string;
  uuid: string;
  email: string;
  iat: number;
  exp: number;
}
