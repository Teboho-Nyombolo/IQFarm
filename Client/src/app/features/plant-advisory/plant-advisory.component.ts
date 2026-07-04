import { Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PlantAdvisoryService, PlantAdvisoryRequest, PlantAdvisoryResponse } from '../../core/services/plant-advisory.service';
import { AuthService } from '../../core/services/auth.service';
import { FarmService } from '../../core/services/farm.service';

@Component({
  selector: 'app-plant-advisory',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './plant-advisory.component.html',
  styleUrls: ['./plant-advisory.component.css']
})
export class PlantAdvisoryComponent {
  readonly #fb = inject(FormBuilder);
  readonly #router = inject(Router);
  readonly #plantAdvisoryService = inject(PlantAdvisoryService);
  readonly #authService = inject(AuthService);
  readonly #farmService = inject(FarmService);
  readonly #platformId = inject(PLATFORM_ID);

  advisoryForm: FormGroup;
  isLoading = signal(false);
  advisoryResponse = signal<PlantAdvisoryResponse | null>(null);
  farmId = signal<number | null>(null);

  constructor() {
    this.advisoryForm = this.#fb.group({
      plantName: ['', Validators.required],
      location: [''],
      season: [''],
      soilType: [''],
      soilHealth: ['']
    });
  }

  ngOnInit() {
    if (isPlatformBrowser(this.#platformId)) {
      this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
        next: (farm) => {
          if (farm) {
            this.farmId.set(farm.farmId);
            this.advisoryForm.patchValue({ location: farm.farmAddress });
          }
        },
        error: (err) => console.error(err)
      });
    }
  }

  submitAdvisoryRequest() {
    if (this.advisoryForm.invalid) {
      this.advisoryForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    const request: PlantAdvisoryRequest = {
      plantName: this.advisoryForm.value.plantName,
      farmId: this.farmId() ?? undefined,
      location: this.advisoryForm.value.location,
      season: this.advisoryForm.value.season,
      soilType: this.advisoryForm.value.soilType,
      soilHealth: this.advisoryForm.value.soilHealth
    };

    this.#plantAdvisoryService.getAdvisory(request).subscribe({
      next: (response) => {
        this.advisoryResponse.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error getting plant advisory', error);
        alert('Failed to get plant advisory');
        this.isLoading.set(false);
      }
    });
  }

  resetForm() {
    this.advisoryForm.reset();
    this.advisoryResponse.set(null);
    if (this.farmId()) {
      this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
        next: (farm) => {
          if (farm) this.advisoryForm.patchValue({ location: farm.farmAddress });
        }
      });
    }
  }
}
