import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageShellComponent } from '../page-shell.component';

/**
 * Reusable "module coming soon" placeholder for feature stubs.
 * Heading comes from route data: `data: { heading: 'Users Admin' }`.
 */
@Component({
  selector: 'app-soon',
  standalone: true,
  imports: [PageShellComponent],
  template: `
    <app-page-shell>
      <section class="soon">
        <div class="icon">🚧</div>
        <h1>{{ heading }}</h1>
        <p>This module is under construction and will be wired to its API soon.</p>
      </section>
    </app-page-shell>
  `,
  styles: [
    `
      .soon {
        text-align: center;
        padding: 64px 0;
        max-width: 480px;
        margin: 0 auto;
      }
      .icon {
        font-size: 56px;
        margin-bottom: 12px;
      }
      h1 {
        color: #cc0b1d;
        font-size: 32px;
        font-weight: 900;
        margin: 0 0 10px;
      }
      p {
        color: #625655;
        font-size: 15px;
        line-height: 1.6;
        margin: 0;
      }
    `,
  ],
})
export class SoonComponent {
  private readonly route = inject(ActivatedRoute);
  protected readonly heading: string =
    this.route.snapshot.data['heading'] ?? 'Coming soon';
}

