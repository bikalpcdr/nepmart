import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface HeroSlide {
  eyebrow: string;
  brandA: string;
  brandB: string;
  title: string;
  descA: string;
  descB: string;
  cta: string;
  link: string;
  image: string;
  alt: string;
}

const SLIDES: HeroSlide[] = [
  {
    eyebrow: 'SHOP LOCAL • SUPPORT NEPAL',
    brandA: 'Nep',
    brandB: 'Mart',
    title: 'Your One Stop Online Market',
    descA: 'Discover quality products from local and trusted vendors.',
    descB: 'From daily essentials to unique Nepali items — all in one place.',
    cta: 'Shop Now',
    link: '/shop',
    image:
      'https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=1800&q=80',
    alt: 'Himalayan mountains over a Kathmandu pagoda temple at sunrise',
  },
  {
    eyebrow: 'FRESH & HEALTHY • DAILY ESSENTIALS',
    brandA: 'Nep',
    brandB: 'Mart',
    title: 'Organic Groceries, Delivered Fresh',
    descA: 'Farm-fresh vegetables and pantry staples from local growers.',
    descB: 'Quality-checked every morning before dispatch.',
    cta: 'Shop Groceries',
    link: '/shop/groceries',
    image:
      'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1800&q=80',
    alt: 'Fresh organic vegetables at a market',
  },
  {
    eyebrow: 'TOP BRANDS • BEST DEALS',
    brandA: 'Nep',
    brandB: 'Mart',
    title: 'Latest Electronics & Gadgets',
    descA: 'Smartphones, laptops and accessories from top brands.',
    descB: 'Genuine products with warranty and easy returns.',
    cta: 'Explore Electronics',
    link: '/shop/electronics',
    image:
      'https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=1800&q=80',
    alt: 'Latest electronics and gadgets',
  },
];

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css',
})
export class HeroComponent implements OnInit, OnDestroy {
  protected readonly slides = SLIDES;
  protected readonly active = signal(0);
  private timer: ReturnType<typeof setInterval> | undefined;

  ngOnInit(): void {
    this.timer = setInterval(() => {
      this.active.update((i) => (i + 1) % SLIDES.length);
    }, 6000);
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }

  protected go(i: number): void {
    this.active.set(i);
  }
}
