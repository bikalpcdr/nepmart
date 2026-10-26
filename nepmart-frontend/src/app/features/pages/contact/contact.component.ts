import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PageShellComponent } from '../page-shell.component';

/** Static Contact page — contact details plus a message form (client-side demo; no backend yet). */
@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [PageShellComponent, ReactiveFormsModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css',
})
export class ContactComponent {
  private readonly fb = inject(FormBuilder);

  protected sent = false;
  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    subject: ['', Validators.required],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    // Static demo: no contact API exists yet. Show confirmation and reset.
    this.sent = true;
    this.form.reset();
  }
}
