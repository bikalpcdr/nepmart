import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { requirePermissions } from './core/auth/permission.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home.component').then((m) => m.HomeComponent),
    title: 'Nepmart — Your one-stop shop for everything Nepali',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login.component').then((m) => m.LoginComponent),
    title: 'Sign In — Nepmart',
  },
  {
    path: 'signup',
    loadComponent: () =>
      import('./features/auth/signup.component').then((m) => m.SignupComponent),
    title: 'Create Account — Nepmart',
  },
  // Public catalog (placeholder until Products API lands).
  {
    path: 'shop',
    loadComponent: () =>
      import('./features/products/catalog.component').then((m) => m.CatalogComponent),
    title: 'Shop — Nepmart',
  },
  {
    path: 'shop/:category',
    loadComponent: () =>
      import('./features/products/catalog.component').then((m) => m.CatalogComponent),
    title: 'Shop — Nepmart',
  },
  {
    path: 'categories',
    loadComponent: () =>
      import('./features/categories/categories.component').then((m) => m.CategoriesComponent),
    title: 'Categories — Nepmart',
  },
  {
    path: 'vendors',
    loadComponent: () =>
      import('./features/vendors/vendors.component').then((m) => m.VendorsComponent),
    title: 'Vendors — Nepmart',
  },
  // Static pages.
  {
    path: 'about',
    loadComponent: () =>
      import('./features/pages/about/about.component').then((m) => m.AboutComponent),
    title: 'About Us — Nepmart',
  },
  {
    path: 'contact',
    loadComponent: () =>
      import('./features/pages/contact/contact.component').then((m) => m.ContactComponent),
    title: 'Contact — Nepmart',
  },
  {
    path: 'faq',
    loadComponent: () =>
      import('./features/pages/faq/faq.component').then((m) => m.FaqComponent),
    title: 'FAQ — Nepmart',
  },
  {
    path: 'track',
    loadComponent: () =>
      import('./features/pages/track/track.component').then((m) => m.TrackComponent),
    title: 'Track Order — Nepmart',
  },
  {
    path: 'returns',
    loadComponent: () =>
      import('./features/pages/returns/returns.component').then((m) => m.ReturnsComponent),
    title: 'Returns & Refunds — Nepmart',
  },
  // Authenticated shopper pages.
  {
    path: 'me',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/pages/account/account.component').then((m) => m.AccountComponent),
    title: 'My Account — Nepmart',
  },
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/cart/cart.component').then((m) => m.CartComponent),
    title: 'Cart — Nepmart',
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/checkout/checkout.component').then((m) => m.CheckoutComponent),
    title: 'Checkout — Nepmart',
  },
  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/orders/orders.component').then((m) => m.OrdersComponent),
    title: 'My Orders — Nepmart',
  },
  {
    path: 'wishlist',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/wishlist/wishlist.component').then((m) => m.WishlistComponent),
    title: 'Wishlist — Nepmart',
  },
  {
    path: 'payments',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/payments/payments.component').then((m) => m.PaymentsComponent),
    title: 'Payments — Nepmart',
  },
  {
    path: 'vendor-dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/vendor-dashboard/vendor-dashboard.component').then(
        (m) => m.VendorDashboardComponent,
      ),
    title: 'Vendor Dashboard — Nepmart',
  },
  // Admin panel (sidebar layout; permission-gated to match the navbar link).
  {
    path: 'admin',
    canActivate: [authGuard, requirePermissions('user:read')],
    loadComponent: () =>
      import('./layout/admin-layout/admin-layout.component').then(
        (m) => m.AdminLayoutComponent,
      ),
    title: 'Admin — Nepmart',
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'users' },
      {
        path: 'users',
        title: 'Users — Admin — Nepmart',
        canActivate: [requirePermissions('user:read')],
        loadComponent: () =>
          import('./features/admin/admin-users.component').then(
            (m) => m.AdminUsersComponent,
          ),
      },
      {
        path: 'vendors',
        title: 'Vendors — Admin — Nepmart',
        canActivate: [requirePermissions('vendor:read')],
        loadComponent: () =>
          import('./features/admin/admin-vendors.component').then(
            (m) => m.AdminVendorsComponent,
          ),
      },
      {
        path: 'roles',
        title: 'Roles — Admin — Nepmart',
        canActivate: [requirePermissions('role:read')],
        loadComponent: () =>
          import('./features/admin/admin-roles.component').then(
            (m) => m.AdminRolesComponent,
          ),
      },
      {
        path: 'permissions',
        title: 'Permissions — Admin — Nepmart',
        canActivate: [requirePermissions('permission:read')],
        loadComponent: () =>
          import('./features/admin/admin-permissions.component').then(
            (m) => m.AdminPermissionsComponent,
          ),
      },
    ],
  },
  {
    path: '**',
    loadComponent: () =>
      import('./features/pages/not-found/not-found.component').then(
        (m) => m.NotFoundComponent,
      ),
    title: 'Page Not Found — Nepmart',
  },
];
