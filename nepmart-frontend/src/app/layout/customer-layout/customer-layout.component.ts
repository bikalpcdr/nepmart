import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

const LINKS = [
  { label: 'Orders', path: '/account/orders' },
  { label: 'Wishlist', path: '/account/wishlist' },
  { label: 'Profile', path: '/account/profile' },
];

/** Sidebar shell for signed-in customer pages. */
@Component({
  selector: 'app-customer-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="shell">
      <aside>
        <h2>My Account</h2>
        <nav>
          @for (l of links; track l.path) {
            <a [routerLink]="l.path">{{ l.label }}</a>
          }
        </nav>
      </aside>
      <main><router-outlet></router-outlet></main>
    </div>
  `,
  styleUrl: './customer-layout.component.css',
})
export class CustomerLayoutComponent {
  protected readonly links = LINKS;
}
