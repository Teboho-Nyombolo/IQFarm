import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { CropService, CropSummary } from '../../core/services/crop.service';
import { AuthServiceService } from '../../core/auth-service.service';
import { FarmServiceService } from '../../core/farm-service.service';

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
  readonly #cropService = inject(CropService);
  readonly #authService = inject(AuthServiceService);
  readonly #farmService = inject(FarmServiceService);

  crops = signal<CropSummary[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    if (isPlatformBrowser(this.#platformId)) {
      this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
        next: (farm) => {
          if (farm) {
            this.#cropService.getCropsByFarm(farm.farmId).subscribe({
              next: (crops) => {
                this.crops.set(crops);
                this.isLoading.set(false);
              },
              error: (err) => {
                console.error('Failed to get crops:', err);
                this.isLoading.set(false);
              }
            });
          } else {
            this.isLoading.set(false);
          }
        },
        error: (err) => {
          console.error('Failed to get farm:', err);
          this.isLoading.set(false);
        }
      });
    } else {
      this.isLoading.set(false);
    }
  }

  addNewCrop(): void {
    this.#router.navigate(['/crops/add']);
  }

  viewCropDetails(cropId: number): void {
    this.#router.navigate(['/crops', cropId]);
  }

  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'text-green-600';
      case 'FAILED':
        return 'text-red-500';
      case 'HARVESTED':
        return 'text-blue-600';
      case 'PLANNING':
        return 'text-yellow-600';
      default:
        return 'text-text';
    }
  }
}
