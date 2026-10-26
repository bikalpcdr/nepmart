import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { HasPermissionDirective } from '../../shared/directives/has-permission.directive';
import { LoaderComponent } from '../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ToastService } from '../../core/services/toast.service';
import { AdminApiService } from './admin-api.service';
import { AdminModalComponent } from './admin-modal.component';
import { ADMIN_STYLES } from './admin.styles';
import type {
  Role,
  User,
  UserRole,
  UserRequest,
  UserStatus,
  Vendor,
} from '../../shared/models/user.model';

const PAGE_SIZE = 10;

/** Admin → Users: paginated account list + CRUD + role assignments. */
@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [
    FormsModule,
    LoaderComponent,
    PaginationComponent,
    AdminModalComponent,
    HasPermissionDirective,
  ],
  template: `
    <div class="page-head">
      <div>
        <h2>Users</h2>
        <p>{{ totalElements() }} total · accounts, access status and role assignments</p>
      </div>
      <button class="btn primary" type="button" *appHasPermission="'user:create'" (click)="openCreate()">
        + New user
      </button>
    </div>

    <form class="toolbar" (ngSubmit)="searchNow()">
      <input type="search" name="q" placeholder="Search by email…" [(ngModel)]="searchValue" />
      <select name="status" [(ngModel)]="statusValue">
        <option value="">All statuses</option>
        @for (s of statuses; track s) {
          <option [value]="s">{{ s }}</option>
        }
      </select>
      <button class="btn ghost" type="submit">Search</button>
      <button class="btn ghost" type="button" (click)="resetFilters()">Reset</button>
    </form>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Email</th>
            <th>Type</th>
            <th>Status</th>
            <th>Active</th>
            <th class="num">Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (r of rows(); track r.uuid) {
            <tr>
              <td class="strong">{{ r.email }}</td>
              <td><span class="badge brand">{{ r.userType }}</span></td>
              <td>
                <span
                  class="badge"
                  [class.ok]="r.status === 'ACTIVE'"
                  [class.warn]="r.status === 'PENDING_VERIFICATION'"
                  [class.danger]="r.status === 'BLOCKED' || r.status === 'INACTIVE'"
                  >{{ r.status }}</span
                >
              </td>
              <td>{{ r.isActive ? 'Yes' : 'No' }}</td>
              <td class="num">
                <span class="actions">
                  <button class="link" type="button" *appHasPermission="'user:update'" (click)="openEdit(r)">
                    Edit
                  </button>
                  <button class="link muted" type="button" *appHasPermission="'user_role:read'" (click)="openRoles(r)">
                    Roles
                  </button>
                  <button class="link danger" type="button" *appHasPermission="'user:delete'" (click)="pendingDelete.set(r)">
                    Delete
                  </button>
                </span>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="state">
                {{ loading() ? 'Loading…' : 'No users match your filters.' }}
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>

    <app-loader [visible]="loading()" />
    @if (totalPages() > 1) {
      <app-pagination [page]="page()" [totalPages]="totalPages()" (pageChange)="onPage($event)" />
    }

    @if (editor(); as ed) {
      <app-admin-modal [title]="ed.mode === 'create' ? 'New user' : 'Edit user'" (close)="editor.set(null)">
        <div class="field">
          <label for="user-email">Email</label>
          <input id="user-email" type="email" name="email" [(ngModel)]="email" required />
        </div>
        <div class="field">
          <label for="user-password">
            Password
            @if (ed.mode === 'edit') {
              <span class="hint">(leave blank to keep current)</span>
            }
          </label>
          <input
            id="user-password"
            type="password"
            name="password"
            autocomplete="new-password"
            [(ngModel)]="password"
          />
        </div>
        <div class="field">
          <label for="user-status">Status</label>
          <select id="user-status" name="status" [(ngModel)]="status">
            @for (s of statuses; track s) {
              <option [value]="s">{{ s }}</option>
            }
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="editor.set(null)">Cancel</button>
          <button class="btn primary" type="button" (click)="save()">
            {{ ed.mode === 'create' ? 'Create user' : 'Save changes' }}
          </button>
        </div>
      </app-admin-modal>
    }

    @if (pendingDelete(); as doomed) {
      <app-admin-modal title="Delete user" (close)="pendingDelete.set(null)">
        <p class="muted">
          Delete <strong>{{ doomed.email }}</strong>? This cannot be undone.
        </p>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="pendingDelete.set(null)">Cancel</button>
          <button class="btn danger" type="button" (click)="confirmDelete()">Delete user</button>
        </div>
      </app-admin-modal>
    }

    @if (rolesFor(); as target) {
      <app-admin-modal [title]="'Roles — ' + target.email" (close)="closeRoles()">
        @if (rolesLoading()) {
          <app-loader />
        } @else {
          @if (assignments().length) {
            <ul class="chip-list">
              @for (a of assignments(); track a.uuid) {
                <li>
                  <span class="chip">
                    {{ a.roleName }}
                    <span class="chip-sub">{{ a.vendorName ?? a.roleCode }}</span>
                  </span>
                  <button class="link danger" type="button" *appHasPermission="'user_role:delete'" (click)="revoke(a)">
                    Revoke
                  </button>
                </li>
              }
            </ul>
          } @else {
            <p class="muted">No roles assigned yet.</p>
          }
          <div class="assign-row" *appHasPermission="'user_role:create'">
            <select name="assignRole" [(ngModel)]="assignRoleUuid">
              <option value="">Select a role…</option>
              @for (role of allRoles(); track role.uuid) {
                <option [value]="role.uuid">{{ role.name }} ({{ role.scope }})</option>
              }
            </select>
            @if (selectedRoleScope() === 'VENDOR') {
              <select name="assignVendor" [(ngModel)]="assignVendorUuid">
                <option value="">Select a vendor…</option>
                @for (v of vendorOptions(); track v.uuid) {
                  <option [value]="v.uuid">{{ v.name }}</option>
                }
              </select>
            }
            <button class="btn primary" type="button" (click)="assign()">Assign</button>
          </div>
        }
      </app-admin-modal>
    }
  `,
  styles: [ADMIN_STYLES],
})
export class AdminUsersComponent {
  private readonly api = inject(AdminApiService);
  private readonly toast = inject(ToastService);

  readonly statuses: UserStatus[] = ['ACTIVE', 'INACTIVE', 'BLOCKED', 'PENDING_VERIFICATION'];

  protected readonly rows = signal<User[]>([]);
  protected readonly loading = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(1);
  protected readonly totalElements = signal(0);

  /** Toolbar filter fields (plain fields — ngModel drives change detection). */
  protected searchValue = '';
  protected statusValue = '';

  protected readonly editor = signal<{ mode: 'create' } | { mode: 'edit'; row: User } | null>(null);
  protected email = '';
  protected password = '';
  protected status: UserStatus = 'ACTIVE';

  protected readonly pendingDelete = signal<User | null>(null);

  protected readonly rolesFor = signal<User | null>(null);
  protected readonly rolesLoading = signal(false);
  protected readonly assignments = signal<UserRole[]>([]);
  protected readonly allRoles = signal<Role[]>([]);
  protected readonly vendorOptions = signal<Vendor[]>([]);
  protected assignRoleUuid = '';
  protected assignVendorUuid = '';

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.api
      .listUsers({
        page: this.page(),
        size: PAGE_SIZE,
        email: this.searchValue.trim() || undefined,
        status: this.statusValue || undefined,
      })
      .subscribe({
        next: (data) => {
          this.rows.set(data.content);
          this.totalElements.set(data.totalElements);
          this.totalPages.set(Math.max(data.totalPages, 1));
          this.loading.set(false);
        },
        error: (err) => {
          this.loading.set(false);
          this.toast.error(err.error?.message ?? 'Failed to load users');
        },
      });
  }

  protected searchNow(): void {
    this.page.set(0);
    this.load();
  }

  protected resetFilters(): void {
    this.searchValue = '';
    this.statusValue = '';
    this.searchNow();
  }

  protected onPage(p: number): void {
    this.page.set(p);
    this.load();
  }

  protected openCreate(): void {
    this.email = '';
    this.password = '';
    this.status = 'ACTIVE';
    this.editor.set({ mode: 'create' });
  }

  protected openEdit(row: User): void {
    this.email = row.email;
    this.password = '';
    this.status = row.status;
    this.editor.set({ mode: 'edit', row });
  }

  protected save(): void {
    const ed = this.editor();
    if (!ed) return;
    const email = this.email.trim();
    if (!email) {
      this.toast.error('Email is required');
      return;
    }
    if (ed.mode === 'create' && this.password.length < 8) {
      this.toast.error('Password must be at least 8 characters');
      return;
    }
    const body: UserRequest = {
      email,
      status: this.status,
      // Omit on edit so the backend keeps the current password.
      ...(this.password ? { password: this.password } : {}),
    };
    const request$ =
      ed.mode === 'create' ? this.api.createUser(body) : this.api.updateUser(ed.row.uuid, body);
    request$.subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'User saved');
        this.editor.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not save user'),
    });
  }

  protected confirmDelete(): void {
    const doomed = this.pendingDelete();
    if (!doomed) return;
    this.api.deleteUser(doomed.uuid).subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'User deleted');
        this.pendingDelete.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not delete user'),
    });
  }

  protected openRoles(user: User): void {
    this.rolesFor.set(user);
    this.rolesLoading.set(true);
    this.assignments.set([]);
    this.assignRoleUuid = '';
    this.assignVendorUuid = '';
    forkJoin({
      assignments: this.api.listUserRoles({ userUuid: user.uuid, size: 100 }),
      roles: this.api.listRoles({ size: 100 }),
      vendors: this.api.listVendors({ size: 100 }),
    }).subscribe({
      next: ({ assignments, roles, vendors }) => {
        this.assignments.set(assignments.content);
        this.allRoles.set(roles.content);
        this.vendorOptions.set(vendors.content);
        this.rolesLoading.set(false);
      },
      error: (err) => {
        this.rolesLoading.set(false);
        this.rolesFor.set(null);
        this.toast.error(err.error?.message ?? 'Could not load role assignments');
      },
    });
  }

  protected closeRoles(): void {
    this.rolesFor.set(null);
  }

  /** Scope of the selected assign-role option (method, not computed, so ngModel plain fields are read fresh). */
  protected selectedRoleScope(): Role['scope'] | null {
    return this.allRoles().find((r) => r.uuid === this.assignRoleUuid)?.scope ?? null;
  }

  protected assign(): void {
    const user = this.rolesFor();
    if (!user) return;
    if (!this.assignRoleUuid) {
      this.toast.error('Select a role to assign');
      return;
    }
    const needsVendor = this.selectedRoleScope() === 'VENDOR';
    if (needsVendor && !this.assignVendorUuid) {
      this.toast.error('This role requires a vendor');
      return;
    }
    this.api
      .assignUserRole({
        userUuid: user.uuid,
        roleUuid: this.assignRoleUuid,
        ...(needsVendor && this.assignVendorUuid ? { vendorUuid: this.assignVendorUuid } : {}),
      })
      .subscribe({
        next: (res) => {
          this.toast.success(res.message ?? 'Role assigned');
          this.assignRoleUuid = '';
          this.assignVendorUuid = '';
          this.reloadAssignments();
        },
        error: (err) => this.toast.error(err.error?.message ?? 'Could not assign role'),
      });
  }

  protected revoke(assignment: UserRole): void {
    this.api.deleteUserRole(assignment.uuid).subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Role revoked');
        this.reloadAssignments();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not revoke role'),
    });
  }

  private reloadAssignments(): void {
    const user = this.rolesFor();
    if (!user) return;
    this.api.listUserRoles({ userUuid: user.uuid, size: 100 }).subscribe({
      next: (page) => this.assignments.set(page.content),
      error: (err) => this.toast.error(err.error?.message ?? 'Could not refresh roles'),
    });
  }
}
