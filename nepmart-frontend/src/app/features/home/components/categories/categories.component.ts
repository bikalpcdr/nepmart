import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface HomeCategory {
  name: string;
  count: string;
  slug: string;
  bg: string;
  color: string;
  icon: 'basket' | 'phone' | 'shirt' | 'home' | 'beauty' | 'ball' | 'toy' | 'book';
}

const CATEGORIES: HomeCategory[] = [
  {
    name: 'Groceries',
    count: '1200+',
    slug: 'groceries',
    bg: '#fde7e7',
    color: '#c2410c',
    icon: 'basket',
  },
  {
    name: 'Electronics',
    count: '850+',
    slug: 'electronics',
    bg: '#dfeafc',
    color: '#1d4ed8',
    icon: 'phone',
  },
  {
    name: 'Fashion',
    count: '1500+',
    slug: 'fashion',
    bg: '#d9f3e6',
    color: '#15803d',
    icon: 'shirt',
  },
  {
    name: 'Home & Living',
    count: '900+',
    slug: 'home-living',
    bg: '#ffe9d6',
    color: '#c2410c',
    icon: 'home',
  },
  {
    name: 'Beauty & Health',
    count: '600+',
    slug: 'beauty-health',
    bg: '#eee3fb',
    color: '#7c3aed',
    icon: 'beauty',
  },
  {
    name: 'Sports & Outdoors',
    count: '400+',
    slug: 'sports-outdoors',
    bg: '#d8f1f1',
    color: '#0e7490',
    icon: 'ball',
  },
  {
    name: 'Toys & Games',
    count: '300+',
    slug: 'toys-games',
    bg: '#fde0e8',
    color: '#db2777',
    icon: 'toy',
  },
  {
    name: 'Books & Stationery',
    count: '500+',
    slug: 'books-stationery',
    bg: '#e3e6fb',
    color: '#4338ca',
    icon: 'book',
  },
];

@Component({
  selector: 'app-home-categories',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css',
})
export class HomeCategoriesComponent {
  protected readonly categories = CATEGORIES;
}
