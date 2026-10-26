import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../../../core/services/toast.service';
import { WishlistStore } from '../../../../core/services/wishlist.store';

interface Product {
  id: string;
  name: string;
  price: string;
  oldPrice?: string;
  discount?: string;
  image: string;
  alt: string;
}

const PRODUCTS: Product[] = [
  {
    id: 'p1',
    name: 'Himalayan Arabica Coffee',
    price: 'Rs. 850',
    oldPrice: 'Rs. 1,000',
    discount: '-15%',
    image:
      'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&w=600&q=80',
    alt: 'Bag of Himalayan Arabica coffee beans',
  },
  {
    id: 'p2',
    name: "Men's Winter Jacket",
    price: 'Rs. 2,699',
    oldPrice: 'Rs. 2,999',
    discount: '-10%',
    image:
      'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=600&q=80',
    alt: "Men's winter jacket",
  },
  {
    id: 'p3',
    name: 'Samsung Galaxy A15',
    price: 'Rs. 18,999',
    image:
      'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=600&q=80',
    alt: 'Samsung Galaxy A15 smartphone',
  },
  {
    id: 'p4',
    name: 'Traditional Brass Bowl',
    price: 'Rs. 1,250',
    image:
      'https://images.unsplash.com/photo-1610701596007-11502861dcfa?auto=format&fit=crop&w=600&q=80',
    alt: 'Traditional Nepali brass bowl',
  },
  {
    id: 'p5',
    name: 'Running Shoes',
    price: 'Rs. 3,499',
    image:
      'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=600&q=80',
    alt: 'Red running shoes',
  },
  {
    id: 'p6',
    name: 'Smart Watch',
    price: 'Rs. 4,999',
    image:
      'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80',
    alt: 'Smart watch',
  },
];

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css',
})
export class ProductsComponent {
  private readonly toast = inject(ToastService);
  protected readonly wishlist = inject(WishlistStore);
  protected readonly products = PRODUCTS;

  protected toggleWishlist(id: string, name: string): void {
    const wasSaved = this.wishlist.has(id);
    this.wishlist.toggle(id);
    this.toast.info(wasSaved ? `${name} removed from wishlist.` : `${name} saved to wishlist.`);
  }

  protected onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/images/logo.png';
  }
}
