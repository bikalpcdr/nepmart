import { Component } from '@angular/core';
import { PageShellComponent } from '../page-shell.component';

/** Static About page — brand story, values, and stats. */
@Component({
  selector: 'app-about',
  standalone: true,
  imports: [PageShellComponent],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css',
})
export class AboutComponent {}
