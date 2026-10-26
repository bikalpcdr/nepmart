import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageShellComponent } from '../page-shell.component';

/** Static Returns & Refunds policy page. */
@Component({
  selector: 'app-returns',
  standalone: true,
  imports: [PageShellComponent, RouterLink],
  templateUrl: './returns.component.html',
  styleUrl: './returns.component.css',
})
export class ReturnsComponent {}
