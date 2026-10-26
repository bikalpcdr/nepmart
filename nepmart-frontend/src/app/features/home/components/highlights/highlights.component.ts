import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Highlight {
  title: string;
  description: string;
  icon: string;
}

const HIGHLIGHTS: Highlight[] = [
  {
    title: 'Fresh Daily',
    description:
      'Farm-fresh produce sourced every morning from trusted local growers across Nepal.',
    icon: '🥬',
  },
  {
    title: 'Quality Assured',
    description: 'Every item is inspected for quality and freshness before it reaches your door.',
    icon: '✓',
  },
  {
    title: 'Quick Delivery',
    description: 'Same-day delivery in Kathmandu Valley. Nationwide shipping coming soon.',
    icon: '🚚',
  },
  {
    title: 'Easy Payments',
    description: 'Pay by cash, e-commerce wallet, or card. Flexible payment options at checkout.',
    icon: '🧾',
  },
  {
    title: 'Hot Discounts',
    description: 'Weekly deals and flash sales on the best-selling products from our catalog.',
    icon: '🔥',
  },
  {
    title: 'Support Local',
    description: 'Your purchase supports local farmers and artisans in villages across Nepal.',
    icon: '🤝',
  },
];

@Component({
  selector: 'app-highlights',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './highlights.component.html',
  styleUrl: './highlights.component.css',
})
export class HighlightsComponent {
  protected readonly highlights = HIGHLIGHTS;
}
