import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { LogoComponent } from '../../shared/components/logo/logo.component';
import { PlantAdvisoryService, PlantAdvisoryRequest, PlantAdvisoryResponse } from '../../core/services/plant-advisory.service';
import { AuthService } from '../../core/services/auth.service';
import { FarmService } from '../../core/services/farm.service';

@Component({
  selector: 'app-seasonal-insights',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NavbarComponent, LogoComponent],
  templateUrl: './seasonal-insights.component.html'
})
export class SeasonalInsightsComponent implements OnInit {
  readonly #fb = inject(FormBuilder);
  readonly #platformId = inject(PLATFORM_ID);
  readonly #advisoryService = inject(PlantAdvisoryService);
  readonly #authService = inject(AuthService);
  readonly #farmService = inject(FarmService);

  form: FormGroup;
  isLoading = signal(false);
  advisory = signal<PlantAdvisoryResponse | null>(null);

  constructor() {
    this.form = this.#fb.group({
      plantName: ['', Validators.required],
      location: [''],
      season: [''],
      soilType: [''],
      soilHealth: ['']
    });
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.#platformId)) {
      this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
        next: (farm) => {
          if (farm) {
            this.form.patchValue({ location: farm.farmAddress });
          }
        }
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isLoading.set(true);
    const request: PlantAdvisoryRequest = {
      plantName: this.form.value.plantName,
      location: this.form.value.location,
      season: this.form.value.season,
      soilType: this.form.value.soilType,
      soilHealth: this.form.value.soilHealth
    };
    this.#advisoryService.getAdvisory(request).subscribe({
      next: (response) => {
        this.advisory.set(response);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        alert('Failed to get insights');
        this.isLoading.set(false);
      }
    });
  }

  reset(): void {
    this.advisory.set(null);
    this.form.patchValue({ plantName: '' });
  }

  getPlantImagePrompt(plantName: string | undefined): string {
    if (!plantName) {
      return encodeURIComponent('agricultural plant in field');
    }
    return encodeURIComponent(`${plantName} plant growing in healthy field, vibrant green, agricultural setting`);
  }

  selectPlant(plantName: string): void {
    this.form.patchValue({ plantName });
  }
}
