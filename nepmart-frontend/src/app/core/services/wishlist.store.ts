import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class WishlistStore {
  private readonly ids = signal<Set<string>>(new Set<string>());

  has(id: string): boolean {
    return this.ids().has(id);
  }

  toggle(id: string): void {
    const next = new Set(this.ids());
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    this.ids.set(next);
  }

  snapshot(): Set<string> {
    return this.ids();
  }
}
