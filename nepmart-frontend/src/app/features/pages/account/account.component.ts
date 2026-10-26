import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SessionService } from '../../../core/auth/session.service';
import { PageShellComponent } from '../page-shell.component';

/**
 * My Account page (`/me`) — renders the session data already loaded from
 * `GET /v1/users/me/init`: user details, assigned roles, vendor context,
 * and permissions. No extra backend calls needed.
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [PageShellComponent, RouterLink],
  templateUrl: './account.component.html',
  styleUrl: './account.component.css',
})
export class AccountComponent {
  protected readonly session = inject(SessionService);
}
