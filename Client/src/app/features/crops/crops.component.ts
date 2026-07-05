import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { LogoComponent } from '../../shared/components/logo/logo.component';
import { CropService, CropSummary } from '../../core/services/crop.service';
import { AuthService } from '../../core/services/auth.service';
import { FarmService } from '../../core/services/farm.service';

@Component({
  selector: 'app-crops',
  standalone: true,
  imports: [CommonModule, NavbarComponent, LogoComponent],
  templateUrl: './crops.component.html',
  styleUrls: ['./crops.component.css']
})
export class CropsComponent implements OnInit {
  readonly #platformId = inject(PLATFORM_ID);
  readonly #router = inject(Router);
  readonly #cropService = inject(CropService);
  readonly #authService = inject(AuthService);
  readonly #farmService = inject(FarmService);

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
        return 'text-[var(--color-primary)]';
      case 'FAILED':
        return 'text-[var(--color-danger)]';
      case 'HARVESTED':
        return 'text-[var(--color-info)]';
      case 'PLANNING':
        return 'text-[var(--color-warning)]';
      default:
        return 'text-[var(--color-text)]';
    }
  }
}
