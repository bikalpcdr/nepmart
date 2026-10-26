import { Injectable, computed, signal } from '@angular/core';

export interface CartLine {
  id: string;
  qty: number;
}

/**
 * Tiny frontend-only cart state.
 * Persists to localStorage so the header badge survives reloads.
 * Real cart API can replace this store later without touching the UI.
 */
@Injectable({ providedIn: 'root' })
export class CartStore {
  private readonly lines = signal<CartLine[]>(CartStore.read());

  readonly count = computed(() => this.lines().reduce((n, l) => n + l.qty, 0));

  add(id: string, qty = 1): void {
    const next = [...this.lines()];
    const found = next.find((l) => l.id === id);
    if (found) {
      found.qty += qty;
    } else {
      next.push({ id, qty });
    }
    this.lines.set(next);
    CartStore.write(next);
  }

  clear(): void {
    this.lines.set([]);
    CartStore.write([]);
  }

  private static key = 'nepmart.cart.v1';

  private static read(): CartLine[] {
    try {
      const raw = localStorage.getItem(CartStore.key);
      if (!raw) return [];
      const parsed = JSON.parse(raw) as CartLine[];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

  private static write(lines: CartLine[]): void {
    try {
      localStorage.setItem(CartStore.key, JSON.stringify(lines));
    } catch {
      /* storage unavailable — ignore */
    }
  }
}
