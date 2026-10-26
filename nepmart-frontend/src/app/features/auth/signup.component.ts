import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';

/** Signup page — POST /v1/auth/register (§3.2). */
@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrl: './login.component.css',
})
export class SignupComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  protected error: string | null = null;
  protected hidePassword = true;

  protected togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  protected readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error = null;
    this.auth.register(this.form.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Account created! Please sign in.');
        this.router.navigateByUrl('/login');
      },
      error: (err) => {
        const message = err.error?.message ?? 'Signup failed. Try again.';
        this.error = message;
        this.toast.error(message);
      },
    });
  }
}
