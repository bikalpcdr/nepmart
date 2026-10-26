import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageShellComponent } from '../page-shell.component';

interface Faq {
  readonly q: string;
  readonly a: string;
}

const FAQ_GROUPS: ReadonlyArray<{ readonly title: string; readonly icon: string; readonly items: readonly Faq[] }> = [
  {
    title: 'Orders & Payment',
    icon: '🛒',
    items: [
      { q: 'How do I place an order?', a: 'Browse categories or search for a product, add items to your cart, and proceed to checkout. You will need a free Nepmart account to complete an order.' },
      { q: 'Which payment methods do you accept?', a: 'We accept eSewa, Khalti, Connect IPS, and cash on delivery in supported areas. Card support is coming soon.' },
      { q: 'Can I modify or cancel my order?', a: 'Orders can be modified or cancelled free of charge until the vendor begins processing them. After that, please use the return flow once the order is delivered.' },
    ],
  },
  {
    title: 'Delivery',
    icon: '🚚',
    items: [
      { q: 'Where do you deliver?', a: 'We deliver across all 7 provinces of Nepal. Same-day and next-day slots are available inside the Kathmandu Valley; other regions are served by courier in 2–5 business days.' },
      { q: 'How much does delivery cost?', a: 'Delivery fees depend on your location and order size. The exact fee is shown at checkout before you confirm the order — no surprises afterwards.' },
      { q: 'How do I track my order?', a: 'Go to Track Order in the footer, enter your order ID and email, and you will see live status from packing to out-for-delivery.' },
    ],
  },
  {
    title: 'Returns & Refunds',
    icon: '↩️',
    items: [
      { q: 'What is your return policy?', a: 'Fresh produce can be reported within 24 hours of delivery if quality is unsatisfactory. Non-perishable items can be returned within 7 days in original packaging.' },
      { q: 'When do I get my refund?', a: 'Once a return is approved and received by the vendor, refunds are issued to your original payment method within 3–5 business days (instant for wallet payments in most cases).' },
    ],
  },
  {
    title: 'Account & Vendors',
    icon: '👤',
    items: [
      { q: 'Is it free to create an account?', a: 'Yes — signing up is completely free and takes under a minute with your email address.' },
      { q: 'How do I sell on Nepmart?', a: 'Contact us via the Contact page or email hello@nepmart.com with your business details. Our vendor team will guide you through onboarding, usually within 48 hours.' },
    ],
  },
];

/** Static FAQ page — grouped, expandable questions (no backend needed). */
@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [PageShellComponent, RouterLink],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.css',
})
export class FaqComponent {
  protected readonly groups = FAQ_GROUPS;
  protected open: string | null = null;

  protected toggle(key: string): void {
    this.open = this.open === key ? null : key;
  }
}
