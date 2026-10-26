import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HasPermissionDirective } from '../../shared/directives/has-permission.directive';
import { LoaderComponent } from '../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ToastService } from '../../core/services/toast.service';
import { AdminApiService } from './admin-api.service';
import { AdminModalComponent } from './admin-modal.component';
import { ADMIN_STYLES } from './admin.styles';
import type { Role, RoleRequest, RoleScope, Vendor } from '../../shared/models/user.model';

const PAGE_SIZE = 10;

/** Admin → Roles: system/vendor role CRUD. */
@Component({
  selector: 'app-admin-roles',
  standalone: true,
  imports: [FormsModule, LoaderComponent, PaginationComponent, AdminModalComponent, HasPermissionDirective],
  template: `
    <div class="page-head">
      <div>
        <h2>Roles</h2>
        <p>{{ totalElements() }} total · roles control what each account can do</p>
      </div>
      <button class="btn primary" type="button" *appHasPermission="'role:create'" (click)="openCreate()">
        + New role
      </button>
    </div>

    <form class="toolbar" (ngSubmit)="searchNow()">
      <input type="search" name="q" placeholder="Search by name…" [(ngModel)]="searchValue" />
      <select name="scope" [(ngModel)]="scopeValue">
        <option value="">All scopes</option>
        @for (s of scopes; track s) {
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
            <th>Name</th>
            <th>Code</th>
            <th>Scope</th>
            <th>Active</th>
            <th class="num">Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (r of rows(); track r.uuid) {
            <tr>
              <td class="strong">{{ r.name }}</td>
              <td>{{ r.code }}</td>
              <td>
                <span class="badge" [class.brand]="r.scope === 'VENDOR'" [class.ok]="r.scope === 'SYSTEM'">
                  {{ r.scope }}
                </span>
                @if (r.vendorName) {
                  <span class="muted"> · {{ r.vendorName }}</span>
                }
              </td>
              <td>{{ r.isActive ? 'Yes' : 'No' }}</td>
              <td class="num">
                <span class="actions">
                  <button class="link" type="button" *appHasPermission="'role:update'" (click)="openEdit(r)">
                    Edit
                  </button>
                  @if (!r.isSystemRole) {
                    <button class="link danger" type="button" *appHasPermission="'role:delete'" (click)="pendingDelete.set(r)">
                      Delete
                    </button>
                  }
                </span>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="state">{{ loading() ? 'Loading…' : 'No roles match your filters.' }}</td>
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
      <app-admin-modal [title]="ed.mode === 'create' ? 'New role' : 'Edit role'" (close)="editor.set(null)">
        <div class="field">
          <label for="role-name">Name</label>
          <input id="role-name" type="text" name="name" [(ngModel)]="name" required />
        </div>
        <div class="field">
          <label for="role-code">Code</label>
          <input id="role-code" type="text" name="code" placeholder="e.g. SHOP_MANAGER" [(ngModel)]="code" required />
        </div>
        <div class="field">
          <label for="role-scope">Scope</label>
          <select id="role-scope" name="scope" [(ngModel)]="scope" (ngModelChange)="onScopeChange($event)">
            @for (s of scopes; track s) {
              <option [value]="s">{{ s }}</option>
            }
          </select>
        </div>
        @if (scope === 'VENDOR') {
          <div class="field">
            <label for="role-vendor">Vendor</label>
            <select id="role-vendor" name="vendorUuid" [(ngModel)]="vendorUuid">
              <option value="">Select a vendor…</option>
              @for (v of vendorOptions(); track v.uuid) {
                <option [value]="v.uuid">{{ v.name }}</option>
              }
            </select>
          </div>
        }
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="editor.set(null)">Cancel</button>
          <button class="btn primary" type="button" (click)="save()">
            {{ ed.mode === 'create' ? 'Create role' : 'Save changes' }}
          </button>
        </div>
      </app-admin-modal>
    }

    @if (pendingDelete(); as doomed) {
      <app-admin-modal title="Delete role" (close)="pendingDelete.set(null)">
        <p class="muted">
          Delete role <strong>{{ doomed.name }}</strong> ({{ doomed.code }})? Users assigned to it lose its
          permissions. This cannot be undone.
        </p>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="pendingDelete.set(null)">Cancel</button>
          <button class="btn danger" type="button" (click)="confirmDelete()">Delete role</button>
        </div>
      </app-admin-modal>
    }
  `,
  styles: [ADMIN_STYLES],
})
export class AdminRolesComponent {
  private readonly api = inject(AdminApiService);
  private readonly toast = inject(ToastService);

  readonly scopes: RoleScope[] = ['SYSTEM', 'VENDOR'];

  protected readonly rows = signal<Role[]>([]);
  protected readonly loading = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(1);
  protected readonly totalElements = signal(0);

  protected searchValue = '';
  protected scopeValue = '';

  protected readonly editor = signal<{ mode: 'create' } | { mode: 'edit'; row: Role } | null>(null);
  protected name = '';
  protected code = '';
  protected scope: RoleScope = 'SYSTEM';
  protected vendorUuid = '';

  protected readonly pendingDelete = signal<Role | null>(null);
  protected readonly vendorOptions = signal<Vendor[]>([]);
  private vendorsLoaded = false;

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.api
      .listRoles({
        page: this.page(),
        size: PAGE_SIZE,
        name: this.searchValue.trim() || undefined,
        scope: this.scopeValue || undefined,
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
          this.toast.error(err.error?.message ?? 'Failed to load roles');
        },
      });
  }

  protected searchNow(): void {
    this.page.set(0);
    this.load();
  }

  protected resetFilters(): void {
    this.searchValue = '';
    this.scopeValue = '';
    this.searchNow();
  }

  protected onPage(p: number): void {
    this.page.set(p);
    this.load();
  }

  protected openCreate(): void {
    this.name = '';
    this.code = '';
    this.scope = 'SYSTEM';
    this.vendorUuid = '';
    this.ensureVendors();
    this.editor.set({ mode: 'create' });
  }

  protected openEdit(row: Role): void {
    this.name = row.name;
    this.code = row.code;
    this.scope = row.scope;
    this.vendorUuid = row.vendorUuid ?? '';
    this.ensureVendors();
    this.editor.set({ mode: 'edit', row });
  }

  protected onScopeChange(value: RoleScope): void {
    this.scope = value;
    if (value === 'VENDOR') this.ensureVendors();
  }

  protected save(): void {
    const ed = this.editor();
    if (!ed) return;
    if (!this.name.trim()) {
      this.toast.error('Role name is required');
      return;
    }
    if (!this.code.trim()) {
      this.toast.error('Role code is required');
      return;
    }
    if (this.scope === 'VENDOR' && !this.vendorUuid) {
      this.toast.error('Select the vendor this role belongs to');
      return;
    }
    const body: RoleRequest = {
      name: this.name.trim(),
      code: this.code.trim(),
      scope: this.scope,
      ...(this.scope === 'VENDOR' && this.vendorUuid ? { vendorUuid: this.vendorUuid } : {}),
    };
    const request$ = ed.mode === 'create' ? this.api.createRole(body) : this.api.updateRole(ed.row.uuid, body);
    request$.subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Role saved');
        this.editor.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not save role'),
    });
  }

  protected confirmDelete(): void {
    const doomed = this.pendingDelete();
    if (!doomed) return;
    this.api.deleteRole(doomed.uuid).subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Role deleted');
        this.pendingDelete.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not delete role'),
    });
  }

  private ensureVendors(): void {
    if (this.vendorsLoaded) return;
    this.api.listVendors({ size: 100 }).subscribe({
      next: (page) => {
        this.vendorOptions.set(page.content);
        this.vendorsLoaded = true;
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not load vendors'),
    });
  }
}
