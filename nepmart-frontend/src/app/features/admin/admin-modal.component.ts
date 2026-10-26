import { Component, input, output } from '@angular/core';

/**
 * Dialog shell used by the admin CRUD pages: backdrop (click to dismiss),
 * header with title + close button, projected body content. Pages provide
 * their own `.modal-actions` footer inside the projected content.
 *
 * ESC also closes the dialog.
 */
@Component({
  selector: 'app-admin-modal',
  standalone: true,
  host: {
    '(document:keydown.escape)': 'close.emit()',
  },
  template: `
    <div class="backdrop" (click)="close.emit()"></div>
    <div class="modal" role="dialog" aria-modal="true" [attr.aria-label]="title()">
      <header>
        <h3>{{ title() }}</h3>
        <button
          type="button"
          class="x"
          (click)="close.emit()"
          aria-label="Close dialog"
        >
          ×
        </button>
      </header>
      <div class="body"><ng-content /></div>
    </div>
  `,
  styles: [
    `
      :host {
        position: fixed;
        inset: 0;
        z-index: 60;
        display: block;
      }
      .backdrop {
        position: absolute;
        inset: 0;
        background: rgba(31, 23, 22, 0.45);
      }
      .modal {
        position: relative;
        margin: 8vh auto 0;
        width: min(480px, calc(100vw - 32px));
        background: #fff;
        border-radius: 16px;
        box-shadow: 0 24px 64px rgba(31, 23, 22, 0.25);
        max-height: 84vh;
        overflow-y: auto;
      }
      header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        padding: 18px 22px 0;
      }
      h3 {
        margin: 0;
        font-size: 17px;
        color: #1f1716;
      }
      .x {
        border: none;
        background: none;
        font-size: 22px;
        line-height: 1;
        color: #625655;
        cursor: pointer;
        padding: 2px 6px;
        border-radius: 8px;
      }
      .x:hover {
        background: #f1eded;
        color: #1f1716;
      }
      .body {
        padding: 16px 22px 22px;
      }
    `,
  ],
})
export class AdminModalComponent {
  readonly title = input.required<string>();
  readonly close = output<void>();
}
