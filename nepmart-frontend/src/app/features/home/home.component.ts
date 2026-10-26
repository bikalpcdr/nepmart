import { Component } from '@angular/core';
import { HeroComponent } from './components/hero/hero.component';
import { HomeCategoriesComponent } from './components/categories/categories.component';
import { HomePromosComponent } from './components/promos/promos.component';
import { ProductsComponent } from './components/products/products.component';
import { HighlightsComponent } from './components/highlights/highlights.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HeroComponent,
    HomeCategoriesComponent,
    HomePromosComponent,
    ProductsComponent,
    HighlightsComponent,
    FooterComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {}
