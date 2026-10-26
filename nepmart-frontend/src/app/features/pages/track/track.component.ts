import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PageShellComponent } from '../page-shell.component';

/** Static Track Order page — demo lookup UI; real tracking arrives with the Orders API. */
@Component({
  selector: 'app-track',
  standalone: true,
  imports: [PageShellComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './track.component.html',
  styleUrl: './track.component.css',
})
export class TrackComponent {
  private readonly fb = inject(FormBuilder);

  protected searched = false;
  protected readonly form = this.fb.nonNullable.group({
    orderId: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    // Static demo: Orders API not available yet.
    this.searched = true;
  }
}
