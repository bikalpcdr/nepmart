import { Component, inject } from '@angular/core';
import { Toast, ToastService } from '../../../core/services/toast.service';

/** Global toast host — renders `ToastService` messages. Mounted once in the root App. */
@Component({
  selector: 'app-toasts',
  standalone: true,
  template: `
    <div class="toasts" aria-live="polite">
      @for (t of toast.toasts(); track t.id) {
        <div class="toast" [class]="'toast ' + t.kind" role="alert">
          <span class="msg">{{ t.message }}</span>
          <button class="close" type="button" (click)="toast.dismiss(t.id)" aria-label="Dismiss">
            ×
          </button>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .toasts {
        position: fixed;
        bottom: 24px;
        right: 24px;
        z-index: 1000;
        display: flex;
        flex-direction: column;
        gap: 10px;
        max-width: 360px;
      }
      .toast {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        gap: 12px;
        border-radius: 12px;
        padding: 13px 16px;
        font-size: 14px;
        font-weight: 600;
        color: #fff;
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.18);
        animation: slide-in 0.25s ease;
      }
      .toast.success {
        background: #508c3c;
      }
      .toast.error {
        background: #cc0b1d;
      }
      .toast.info {
        background: #64a0c8;
      }
      .msg {
        flex: 1;
      }
      .close {
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.85);
        font-size: 18px;
        line-height: 1;
        cursor: pointer;
        padding: 0;
      }
      .close:hover {
        color: #fff;
      }
      @keyframes slide-in {
        from {
          opacity: 0;
          transform: translateX(24px);
        }
        to {
          opacity: 1;
          transform: translateX(0);
        }
      }
    `,
  ],
})
export class ToastComponent {
  protected readonly toast = inject(ToastService);
}
