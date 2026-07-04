import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

interface Crop {
  id: string;
  name: string;
  location: string;
  image: string;
  status: 'healthy' | 'needs-water' | 'alert';
  statusLabel: string;
  metrics: { label: string; value: string; color?: string }[];
}

@Component({
  selector: 'app-crops',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './crops.component.html',
  styleUrls: ['./crops.component.css']
})
export class CropsComponent implements OnInit {
  readonly #platformId = inject(PLATFORM_ID);
  readonly #router = inject(Router);
  crops: Crop[] = [
    {
      id: '1',
      name: 'Golden Corn',
      location: 'Field A-12',
      image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBooNc6TzqYp1KnW15vIkiqUVLchccShO3g_Kz_cuL0fsn9w5jP7C_HHIlBx7U3e47jUFAEy0WelTC__pjnDUF1l2gFYY1_niN1pWrAiQVFL2b7b4xXFTZ2aHOx0_gopOkrstgNL2pehmoeDdVQ3ciPMnAHeZAESShiz8OBJdfqxsIgG8cZ2T9QjdqulO2YjzTW42zoY0iXZwvG4EMjzDArTY1JLZ94RkTtWrZI7SSKWJg50kZiXcb1',
      status: 'healthy',
      statusLabel: 'Healthy',
      metrics: [
        { label: 'MOISTURE', value: '68%', color: 'text-primary' },
        { label: 'TEMP', value: '24°C' }
      ]
    },
    {
      id: '2',
      name: 'Cherry Tomatoes',
      location: 'Greenhouse 4',
      image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAh_bxZbBa5Za7vyX-dZz-2JMbl5YKxoTBeY3G_sGHI2Pe4Ud5b6tkYlgGsYmrz30GhdCZzwmR3q8OUIzVXzLXkQUACw-QWmjXjE-ZGQoaOx5tdlMiJ6Q8YJJPAUK3QibbHucMUeje-V9fBAYTsIjZWh-o2FUCHYCQtFKPazev515T2TDasi_94gBphQ87yiI7ixZWGYipMzYsf29w39FoPHLrcvjFEP0un9n7IKAUP4RCfrPPoTtv3',
      status: 'needs-water',
      statusLabel: 'Needs Water',
      metrics: [
        { label: 'MOISTURE', value: '42%', color: 'text-error' },
        { label: 'PH LEVEL', value: '6.2' }
      ]
    },
    {
      id: '3',
      name: 'Winter Wheat',
      location: 'Sector C-9',
      image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCkuGmm1jNGgicQtRgHhbxRgSn-r5LGi32pHDfthE1Zjkn_nH5Y-dZ55Wfaie09K44uXmW1tJCq3HJ26WBaBPlRqNIFzCFqckfY-sWKym6X1L-QEENNqjbVNR7EuqeDyFIg7BZlRKoQVqm0Hpfoc5OTzwVHBYzb57kyO3P42gogP2cMFhhCPDAzPSmTFmSiiBZI79XXFl9WBHS4JoaqSQqknw1bu3LV4DAGWqXnOL6tWmtTdOOTHI6S',
      status: 'alert',
      statusLabel: 'Alert',
      metrics: [
        { label: 'PEST RISK', value: 'High', color: 'text-error' },
        { label: 'MATURITY', value: '82%' }
      ]
    }
  ];


  ngOnInit(): void {
    if (isPlatformBrowser(this.#platformId)) {
      window.addEventListener('scroll', () => {
        const header = document.querySelector('header');
        if (window.scrollY > 20) {
          header?.classList.add('backdrop-blur-md', 'bg-opacity-95', 'shadow-md');
        } else {
          header?.classList.remove('backdrop-blur-md', 'bg-opacity-95', 'shadow-md');
        }
      });
    }
  }

  getStatusClasses(status: string): string {
    switch (status) {
      case 'healthy':
        return 'bg-primary-fixed text-on-primary-container';
      case 'needs-water':
        return 'bg-tertiary-fixed text-on-tertiary-container';
      case 'alert':
        return 'bg-error-container text-on-error-container';
      default:
        return 'bg-surface-container text-on-surface';
    }
  }

  getStatusDot(status: string): string {
    switch (status) {
      case 'healthy':
        return 'bg-primary';
      case 'needs-water':
        return 'bg-tertiary';
      case 'alert':
        return 'bg-error';
      default:
        return 'bg-outline';
    }
  }

  onCardTouchStart(event: TouchEvent): void {
    const card = event.currentTarget as HTMLElement;
    card.style.transform = 'scale(0.985)';
    card.style.transition = 'transform 0.15s ease';
  }

  onCardTouchEnd(event: TouchEvent): void {
    const card = event.currentTarget as HTMLElement;
    card.style.transform = 'scale(1)';
  }

  viewDetails(cropId: string): void {
    console.log('View details for crop:', cropId);
  }

  addNewCrop(): void {
    this.#router.navigate(['/crops/add']);
  }
}