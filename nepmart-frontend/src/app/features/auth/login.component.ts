import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';

/** Login page — POST /v1/auth/login (§3.1). Dev seed: nepmart@yopmail.com / Test@1234. */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  protected error: string | null = null;
  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error = null;
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Welcome back! You are signed in.');
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        const message = err.error?.message ?? 'Login failed. Try again.';
        this.error = message;
        this.toast.error(message);
      },
    });
  }
}
