import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loader',
  standalone: true,
  template: `@if (visible()) {
    <div class="loader" role="status" aria-label="Loading">
      <span class="spinner"></span>
    </div>
  }`,
  styles: [
    `
      .loader {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 32px;
      }
      .spinner {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        border: 3px solid rgba(204, 11, 29, 0.2);
        border-top-color: #cc0b1d;
        animation: spin 0.8s linear infinite;
      }
      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }
    `,
  ],
})
export class LoaderComponent {
  readonly visible = input(true);
}
