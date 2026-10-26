import { Component, inject, signal, computed } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { SessionService } from '../../../core/auth/session.service';
import { CartStore } from '../../../core/services/cart.store';
import { ToastService } from '../../../core/services/toast.service';

interface NavLink {
  label: string;
  path: string;
}

/** Public + shared top-navigation links (shown when not signed in, plus shared links). */
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

  /** Shared public links that always appear. */
  protected readonly navLinks = computed(() => {
    const links = [...NAV_LINKS];
    const user = this.session.user();
    if (user) {
      // Signed in: add account-scoped links based on role/vendor membership.
      if (this.session.isAdmin) {
        links.push({ label: 'Admin', path: '/admin' });
      } else if (this.session.vendors().length > 0 || user.userType === 'VENDOR') {
        links.push({ label: 'Vendor', path: '/vendor-dashboard' });
      }
      links.push({ label: 'My Account', path: '/me' });
    } else {
      // Not signed in: show the public auth entry-points.
      links.push({ label: 'Login', path: '/login' });
      links.push({ label: 'Register', path: '/signup' });
    }
    return links;
  });

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
    this.router.navigateByUrl('/login');
  }
}
