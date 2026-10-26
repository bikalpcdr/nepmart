import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageShellComponent } from '../page-shell.component';

/** 404 page — shown for unmatched routes. */
@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [PageShellComponent, RouterLink],
  templateUrl: './not-found.component.html',
  styleUrl: './not-found.component.css',
})
export class NotFoundComponent {}
