import { Component } from '@angular/core';
import { FooterComponent } from '../../shared/components/footer/footer.component';

/**
 * Shared shell for static pages: centered 1320px content column + footer.
 * The navbar is rendered globally by the root App component.
 */
@Component({
  selector: 'app-page-shell',
  standalone: true,
  imports: [FooterComponent],
  template: `
    <div class="wrap">
      <ng-content />
    </div>
    <app-footer />
  `,
  styles: [
    `
      :host {
        display: block;
        background: #fafbfc;
      }
      .wrap {
        max-width: 1320px;
        margin: 0 auto;
        padding: 56px 24px 72px;
      }
    `,
  ],
})
export class PageShellComponent {}
