import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { SessionService } from '../../../core/auth/session.service';
import { CartStore } from '../../../core/services/cart.store';
import { ToastService } from '../../../core/services/toast.service';

interface NavLink {
  label: string;
  path: string;
}

const NAV_LINKS: NavLink[] = [
  { label: 'Home', path: '/' },
  { label: 'Shop', path: '/shop' },
  { label: 'Vendors', path: '/vendors' },
  { label: 'Deals', path: '/shop' },
  { label: 'About Us', path: '/about' },
  { label: 'Contact', path: '/contact' },
];

const LOCATIONS = ['Kathmandu', 'Pokhara', 'Lalitpur', 'Biratnagar', 'Nepalgunj'];

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  protected readonly auth = inject(AuthService);
  protected readonly session = inject(SessionService);
  protected readonly cart = inject(CartStore);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  protected readonly navLinks = NAV_LINKS;
  protected readonly locations = LOCATIONS;
  protected readonly location = signal('Kathmandu');
  protected readonly locationOpen = signal(false);
  protected readonly searchQuery = signal('');

  protected onSearchInput(value: string): void {
    this.searchQuery.set(value);
  }

  protected submitSearch(): void {
    const q = this.searchQuery().trim();
    this.router.navigate(['/shop'], { queryParams: q ? { q } : {} });
  }

  protected pickLocation(name: string): void {
    this.location.set(name);
    this.locationOpen.set(false);
  }

  protected toggleLocation(): void {
    this.locationOpen.update((v) => !v);
  }

  protected logout(): void {
    this.auth.logout();
    this.toast.info('You have been signed out.');
    this.router.navigateByUrl('/');
  }
}
