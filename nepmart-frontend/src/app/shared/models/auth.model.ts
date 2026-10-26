/** Auth payloads — mirrors docs/API_INTEGRATION_GUIDE.md §3. */
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phoneNumber?: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface RefreshRequest {
  refreshToken: string;
}

/** Decoded access-token claims (§3.4). */
export interface JwtClaims {
  sub: string;
  uuid: string;
  email: string;
  iat: number;
  exp: number;
}
