import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FarmService, FarmRequest } from '../../../core/services/farm.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { LogoComponent } from '../../../shared/components/logo/logo.component';

@Component({
  selector: 'app-farm',
  standalone: true,
  imports: [FormsModule, LogoComponent],
  templateUrl: './farm.component.html',
  styleUrl: './farm.component.css'
})
export class FarmComponent {
  readonly #farmService = inject(FarmService);
  readonly #authService = inject(AuthService);
  readonly #router = inject(Router);

  farm = {
    farmName: '',
    streetAddress: '',
    suburb: '',
    city: '',
    zipCode: ''
  };

  createFarm() {
    const farmRequest: FarmRequest = {
      ownerId: this.#authService.getUserId(),
      farmName: this.farm.farmName,
      farmAddress: `${this.farm.streetAddress}, ${this.farm.suburb}, ${this.farm.city}, ${this.farm.zipCode}`
    };

    this.#farmService.createFarm(farmRequest).subscribe({
      next: (response) => {
        console.log('Farm created successfully:', response);
        alert('Farm created successfully!');
        this.#router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error creating farm:', error);
        alert('Failed to create farm!');
      }
    });
  }
}
