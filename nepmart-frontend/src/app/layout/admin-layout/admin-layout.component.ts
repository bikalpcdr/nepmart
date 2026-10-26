import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

const LINKS = [
  { label: 'Users', path: '/admin/users' },
  { label: 'Vendors', path: '/admin/vendors' },
  { label: 'Roles', path: '/admin/roles' },
  { label: 'Permissions', path: '/admin/permissions' },
];

/** Sidebar shell for admin pages (user/vendor/role/permission modules). */
@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <aside>
        <h2>Admin Panel</h2>
        <nav>
          @for (l of links; track l.path) {
            <a
              [routerLink]="l.path"
              routerLinkActive="active"
              [routerLinkActiveOptions]="{ exact: true }"
              >{{ l.label }}</a
            >
          }
        </nav>
      </aside>
      <main><router-outlet></router-outlet></main>
    </div>
  `,
  styleUrl: './admin-layout.component.css',
})
export class AdminLayoutComponent {
  protected readonly links = LINKS;
}
