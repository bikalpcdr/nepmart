import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HasPermissionDirective } from '../../shared/directives/has-permission.directive';
import { LoaderComponent } from '../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ToastService } from '../../core/services/toast.service';
import { AdminApiService } from './admin-api.service';
import { AdminModalComponent } from './admin-modal.component';
import { ADMIN_STYLES } from './admin.styles';
import type { Vendor, VendorRequest, VendorStatus } from '../../shared/models/user.model';

const PAGE_SIZE = 10;

/** Admin → Vendors: seller store CRUD with lifecycle statuses. */
@Component({
  selector: 'app-admin-vendors',
  standalone: true,
  imports: [FormsModule, LoaderComponent, PaginationComponent, AdminModalComponent, HasPermissionDirective],
  template: `
    <div class="page-head">
      <div>
        <h2>Vendors</h2>
        <p>{{ totalElements() }} total · stores selling on the marketplace</p>
      </div>
      <button class="btn primary" type="button" *appHasPermission="'vendor:create'" (click)="openCreate()">
        + New vendor
      </button>
    </div>

    <form class="toolbar" (ngSubmit)="searchNow()">
      <input type="search" name="q" placeholder="Search by name…" [(ngModel)]="searchValue" />
      <select name="vendorStatus" [(ngModel)]="statusValue">
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
            <th>Name</th>
            <th>Slug</th>
            <th>Status</th>
            <th>Active</th>
            <th class="num">Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (r of rows(); track r.uuid) {
            <tr>
              <td class="strong">{{ r.name }}</td>
              <td>{{ r.slug }}</td>
              <td>
                <span
                  class="badge"
                  [class.ok]="r.status === 'ACTIVE'"
                  [class.warn]="r.status === 'PENDING'"
                  [class.danger]="r.status === 'SUSPENDED' || r.status === 'REJECTED'"
                  [class.muted]="r.status === 'INACTIVE'"
                  >{{ r.status }}</span
                >
              </td>
              <td>{{ r.isActive ? 'Yes' : 'No' }}</td>
              <td class="num">
                <span class="actions">
                  <button class="link" type="button" *appHasPermission="'vendor:update'" (click)="openEdit(r)">
                    Edit
                  </button>
                  <button class="link danger" type="button" *appHasPermission="'vendor:delete'" (click)="pendingDelete.set(r)">
                    Delete
                  </button>
                </span>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="state">{{ loading() ? 'Loading…' : 'No vendors match your filters.' }}</td>
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
      <app-admin-modal [title]="ed.mode === 'create' ? 'New vendor' : 'Edit vendor'" (close)="editor.set(null)">
        <div class="field">
          <label for="vendor-name">Name</label>
          <input id="vendor-name" type="text" name="name" [(ngModel)]="name" required />
        </div>
        <div class="field">
          <label for="vendor-slug">
            Slug <span class="hint">(optional — used in URLs)</span>
          </label>
          <input id="vendor-slug" type="text" name="slug" placeholder="e.g. Himalayan Crafts" [(ngModel)]="slug" />
        </div>
        <div class="field">
          <label for="vendor-status">Status</label>
          <select id="vendor-status" name="status" [(ngModel)]="status">
            @for (s of statuses; track s) {
              <option [value]="s">{{ s }}</option>
            }
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="editor.set(null)">Cancel</button>
          <button class="btn primary" type="button" (click)="save()">
            {{ ed.mode === 'create' ? 'Create vendor' : 'Save changes' }}
          </button>
        </div>
      </app-admin-modal>
    }

    @if (pendingDelete(); as doomed) {
      <app-admin-modal title="Delete vendor" (close)="pendingDelete.set(null)">
        <p class="muted">
          Delete vendor <strong>{{ doomed.name }}</strong>? This cannot be undone.
        </p>
        <div class="modal-actions">
          <button class="btn ghost" type="button" (click)="pendingDelete.set(null)">Cancel</button>
          <button class="btn danger" type="button" (click)="confirmDelete()">Delete vendor</button>
        </div>
      </app-admin-modal>
    }
  `,
  styles: [ADMIN_STYLES],
})
export class AdminVendorsComponent {
  private readonly api = inject(AdminApiService);
  private readonly toast = inject(ToastService);

  readonly statuses: VendorStatus[] = ['PENDING', 'ACTIVE', 'SUSPENDED', 'REJECTED', 'INACTIVE'];

  protected readonly rows = signal<Vendor[]>([]);
  protected readonly loading = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(1);
  protected readonly totalElements = signal(0);

  protected searchValue = '';
  protected statusValue = '';

  protected readonly editor = signal<{ mode: 'create' } | { mode: 'edit'; row: Vendor } | null>(null);
  protected name = '';
  protected slug = '';
  protected status: VendorStatus = 'PENDING';

  protected readonly pendingDelete = signal<Vendor | null>(null);

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.api
      .listVendors({
        page: this.page(),
        size: PAGE_SIZE,
        name: this.searchValue.trim() || undefined,
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
          this.toast.error(err.error?.message ?? 'Failed to load vendors');
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
    this.name = '';
    this.slug = '';
    this.status = 'PENDING';
    this.editor.set({ mode: 'create' });
  }

  protected openEdit(row: Vendor): void {
    this.name = row.name;
    this.slug = row.slug;
    this.status = row.status;
    this.editor.set({ mode: 'edit', row });
  }

  protected save(): void {
    const ed = this.editor();
    if (!ed) return;
    if (!this.name.trim()) {
      this.toast.error('Vendor name is required');
      return;
    }
    const body: VendorRequest = {
      name: this.name.trim(),
      slug: this.slug.trim() || undefined,
      status: this.status,
    };
    const request$ = ed.mode === 'create' ? this.api.createVendor(body) : this.api.updateVendor(ed.row.uuid, body);
    request$.subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Vendor saved');
        this.editor.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not save vendor'),
    });
  }

  protected confirmDelete(): void {
    const doomed = this.pendingDelete();
    if (!doomed) return;
    this.api.deleteVendor(doomed.uuid).subscribe({
      next: (res) => {
        this.toast.success(res.message ?? 'Vendor deleted');
        this.pendingDelete.set(null);
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message ?? 'Could not delete vendor'),
    });
  }
}
