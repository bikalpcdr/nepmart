import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

const LINKS = [
  { label: 'Overview', path: '/vendor' },
  { label: 'Products', path: '/vendor/products' },
  { label: 'Orders', path: '/vendor/orders' },
];

/** Sidebar shell for vendor dashboard pages. */
@Component({
  selector: 'app-vendor-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="shell">
      <aside>
        <h2>Vendor Dashboard</h2>
        <nav>
          @for (l of links; track l.path) {
            <a [routerLink]="l.path">{{ l.label }}</a>
          }
        </nav>
      </aside>
      <main><router-outlet></router-outlet></main>
    </div>
  `,
  styleUrl: './vendor-layout.component.css',
})
export class VendorLayoutComponent {
  protected readonly links = LINKS;
}
