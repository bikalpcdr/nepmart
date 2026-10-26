import { Component } from '@angular/core';

/** Products catalog — wired to GET /v1/products when the backend module lands. */
@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css',
})
export class CatalogComponent {}
