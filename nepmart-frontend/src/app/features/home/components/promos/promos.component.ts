import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Promo {
  eyebrow?: string;
  title: string;
  desc: string;
  cta: string;
  link: string;
  theme: 'p1' | 'p2' | 'p3';
  image: string;
  alt: string;
}

const PROMOS: Promo[] = [
  {
    eyebrow: 'FRESH & HEALTHY',
    title: 'Organic Groceries',
    desc: 'Good food. Better life.',
    cta: 'Shop Now',
    link: '/shop/groceries',
    theme: 'p1',
    image:
      'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80',
    alt: 'Fresh organic vegetables and produce',
  },
  {
    title: 'Latest Electronics',
    desc: 'Top brands. Best deals.',
    cta: 'Explore',
    link: '/shop/electronics',
    theme: 'p2',
    image:
      'https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=800&q=80',
    alt: 'Laptop, smartphone and headphones',
  },
  {
    title: 'Authentic Nepali Products',
    desc: 'Support local artisans.',
    cta: 'Shop Now',
    link: '/shop/nepali-products',
    theme: 'p3',
    image:
      'https://images.unsplash.com/photo-1601121141461-9d6647bca1ed?auto=format&fit=crop&w=800&q=80',
    alt: 'Authentic Nepali handicrafts and brass items',
  },
];

@Component({
  selector: 'app-home-promos',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './promos.component.html',
  styleUrl: './promos.component.css',
})
export class HomePromosComponent {
  protected readonly promos = PROMOS;
}
