import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HasPermissionDirective } from '../../shared/directives/has-permission.directive';
import { LoaderComponent } from '../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ToastService } from '../../core/services/toast.service';
import { AdminApiService } from './admin-api.service';
import { AdminModalComponent } from './admin-modal.component';
import { ADMIN_STYLES } from './admin.styles';
import type { Permission, PermissionRequest } from '../../shared/models/user.model';

const PAGE_SIZE = 10;

/** Admin → Permissions: the permission catalogue behind every role. */
@Component({
  selector: 'app-admin-permissions',
  standalone: true,
  imports: [FormsModule, LoaderComponent, PaginationComponent, AdminModalComponent, HasPermissionDirective],
  template: `
    <div class="page-head">
      <div>
        <h2>Permissions</h2>
        <p>{{ totalElements() }} total · module:action grants checked by every guard</p>
      </div>
      <button class="btn primary" type="button" *appHasPermission="'permission:create'" (click)="openCreate()">
        + New permission
      </button>
    </div>

    <form class="toolbar" (ngSubmit)="searchNow()">
      <input type="search" name="q" placeholder="Search by name or code…" [(ngModel)]="searchValue" />
      <input type="search" name="module" placeholder="Filter by module…" [(ngModel)]="moduleValue" />
      <button class="btn ghost" type="submit">Search</button>
      <button class="btn ghost" type="button" (click)="resetFilters()">Reset</button>
    </form>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Code</th>
            <th>Module</th>
            <th>Action</th>
            <th>Active</th>
            <th class="num">Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (r of rows(); track r.uuid) {
            <tr>
              <td class="strong">{{ r.name }}</td>
              <td>{{ r.code }}</td>
              <td><span class="badge brand">{{ r.module }}</span></td>
              <td>{{ r.action }}</td>
              <td>{{ r.isActive ? 'Yes' : 'No' }}</td>
              <td class="num">
                <span class="actions">
                  <button class="link" type="button" *appHasPermission="'permission:update'" (click)="openEdit(r)">
                    Edit
                  </button>
                  <button class="link danger" type="button" *appHasPermission="'permission:delete'" (click)="pendingDelete.set(r)">
                    Delete
                  </button>
                </span>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="state">
                {{ loading() ? 'Loading…' : 'No permissions match your filters.' }}
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
      <app-admin-modal
        [title]="ed.mode === 'create' ? 'New permission' : 'Edit permission'"
        (close)="editor.set(null)"
      >
        <div class="field">
          <label for="perm-name">Name</label>
          <input id="perm-name" type="text" name="name" placeholder="e.g. read orders" [(ngModel)]="name" required />
        </div>
        <div class="field">
          <label for="perm-code">Code</label>
          <input id="perm-code" type="text" name="code" placeholder="e.g. order:read" [(ngModel)]="code" required />
        </div>
        <div class="field">
          <label for="perm-module">Module</label>
          <input id="perm-module" type="text" name="module" placeholder="e.g. order" [(ngModel)]="module" required />
        </div>
        <div class="field">
          <label for="perm-action">Action</label>
          <input id="perm-action" type="text" name="action" placeholder="e.g. read" [(ngModel)]="action" required />
        </div>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="editor.set(null)">Cancel</button>
          <button class="btn primary" type="button" (click)="save()">
            {{ ed.mode === 'create' ? 'Create permission' : 'Save changes' }}
          </button>
        </div>
      </app-admin-modal>
    }

    @if (pendingDelete(); as doomed) {
      <app-admin-modal title="Delete permission" (close)="pendingDelete.set(null)">
        <p class="muted">
          Delete <strong>{{ doomed.code }}</strong>? Roles that use it will lose this grant. This cannot be
          undone.
        </p>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="pendingDelete.set(null)">Cancel</button>
          <button class="btn danger" type="button" (click)="confirmDelete()">Delete permission</button>
        </div>
      </app-admin-modal>
    }
  `,
  styles: [ADMIN_STYLES],
})
export class AdminPermissionsComponent {
  private readonly api = inject(AdminApiService);
  private readonly toast = inject(ToastService);

  protected readonly rows = signal<Permission[]>([]);
  protected readonly loading = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(1);
  protected readonly totalElements = signal(0);

  protected searchValue = '';
  protected moduleValue = '';

  protected readonly editor = signal<{ mode: 'create' } | { mode: 'edit'; row: Permission } | null>(null);
  protected name = '';
  protected code = '';
  protected module = '';
  protected action = '';

  protected readonly pendingDelete = signal<Permission | null>(null);

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.api
      .listPermissions({
        page: this.page(),
        size: PAGE_SIZE,
        name: this.searchValue.trim() || undefined,
        module: this.moduleValue.trim() || undefined,
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
          this.toast.error(err.error?.message ?? 'Failed to load permissions');
        },
      });
  }

  protected searchNow(): void {
    this.page.set(0);
    this.load();
  }

  protected resetFilters(): void {
    this.searchValue = '';
    this.moduleValue = '';
    this.searchNow();
  }

  protected onPage(p: number): void {
    this.page.set(p);
    this.load();
  }

  protected openCreate(): void {
    this.name = '';
    this.code = '';
    this.module = '';
    this.action = '';
    this.editor.set({ mode: 'create' });
  }

  protected openEdit(row: Permission): void {
    this.name = row.name;
    this.code = row.code;
    this.module = row.module;
    this.action = row.action;
    this.editor.set({ mode: 'edit', row });
  }

  protected save(): void {
    const ed = this.editor();
    if (!ed) return;
    if (!this.name.trim() || !this.code.trim() || !this.module.trim() || !this.action.trim()) {
      this.toast.error('Name, code, module and action are all required');
      return;
    }
    const body: PermissionRequest = {
      name: this.name.trim(),
      code: this.code.trim(),
      module: this.module.trim(),
      action: this.action.trim(),
    };
    const request$ =
      ed.mode === 'create' ? this.api.createPermission(body) : this.api.updatePermission(ed.row.uuid, body);
    request$.subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Permission saved');
        this.editor.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not save permission'),
    });
  }

  protected confirmDelete(): void {
    const doomed = this.pendingDelete();
    if (!doomed) return;
    this.api.deletePermission(doomed.uuid).subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Permission deleted');
        this.pendingDelete.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not delete permission'),
    });
  }
}
