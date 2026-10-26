import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';

/** Chrome for public pages (home, shop, auth, …). */
@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, FooterComponent],
  template: `
    <app-navbar></app-navbar>
    <main><router-outlet></router-outlet></main>
    <app-footer></app-footer>
  `,
  styles: [
    `
      main {
        min-height: 60vh;
      }
    `,
  ],
})
export class PublicLayoutComponent {}
