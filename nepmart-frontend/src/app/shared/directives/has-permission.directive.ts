import {
  Directive,
  TemplateRef,
  ViewContainerRef,
  effect,
  inject,
  input,
} from '@angular/core';
import { SessionService } from '../../core/auth/session.service';

/**
 * Shows the host element when the session holds the given permission
 * code(s) from `GET /v1/users/me/init`. Frontend hint only.
 *
 * Usage:
 *   `<button *appHasPermission="'user:create'">Add user</button>`
 *   `<a *appHasPermission="['user:read','role:read']">Admin</a>`
 *   `<button *appHasPermission="'user:delete'; requireAll: true">…</button>`
 */
@Directive({ selector: '[appHasPermission]', standalone: true })
export class HasPermissionDirective {
  readonly permission = input<string | string[]>('', {
    alias: 'appHasPermission',
  });
  readonly requireAll = input(false, {
    alias: 'appHasPermissionRequireAll',
  });

  private readonly session = inject(SessionService);
  private readonly template = inject(TemplateRef<unknown>);
  private readonly view = inject(ViewContainerRef);

  constructor() {
    effect(() => {
      const raw = this.permission();
      const codes = Array.isArray(raw) ? raw : [raw];
      const ok =
        codes.length === 0 ||
        (this.requireAll()
          ? this.session.hasAllPermissions(codes)
          : this.session.hasAnyPermission(codes));
      this.view.clear();
      if (ok) this.view.createEmbeddedView(this.template);
    });
  }
}
