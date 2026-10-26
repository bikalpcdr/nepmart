import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    <nav class="pagination" aria-label="Pagination">
      <button
        class="page-btn"
        [disabled]="page() <= 0"
        (click)="pageChange.emit(page() - 1)"
      >
        Prev
      </button>
      <span class="page-info">
        Page {{ page() + 1 }} of {{ totalPages() }}
      </span>
      <button
        class="page-btn"
        [disabled]="page() + 1 >= totalPages()"
        (click)="pageChange.emit(page() + 1)"
      >
        Next
      </button>
    </nav>
  `,
  styles: [
    `
      .pagination {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        padding: 24px 0;
      }
      .page-btn {
        padding: 8px 18px;
        border-radius: 999px;
        border: 1.5px solid #d7d2d2;
        background: #fff;
        font-weight: 700;
        cursor: pointer;
      }
      .page-btn:disabled {
        opacity: 0.4;
        cursor: default;
      }
      .page-info {
        color: #625655;
        font-size: 14px;
      }
    `,
  ],
})
export class PaginationComponent {
  readonly page = input(0);
  readonly totalPages = input(1);
  readonly pageChange = output<number>();
}
