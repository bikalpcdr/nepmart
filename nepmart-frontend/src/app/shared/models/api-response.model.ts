/**
 * Shared API response envelope — mirrors the Nepmart backend
 * `ApiResponse<T>` + `Page<T>` (see docs/API_INTEGRATION_GUIDE.md §2, §9).
 */
export interface ApiResponse<T> {
  success: boolean;
  timestamp: string;
  message: string | null;
  path: string | null;
  data: T;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
  isEmpty: boolean;
}

export interface ListParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  search?: string;
  [key: string]: string | number | boolean | undefined;
}
