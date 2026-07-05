import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { LogoComponent } from '../../../shared/components/logo/logo.component';
import { CropService, CropSummary } from '../../../core/services/crop.service';
import { DiagnosisService, DiagnosisResponse } from '../../../core/services/diagnosis.service';
import { CropTrackerService, CropTrackerResponse } from '../../../core/services/crop-tracker.service';

@Component({
  selector: 'app-crop-details',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './crop-details.component.html'
})
export class CropDetailsComponent implements OnInit {
  readonly #route = inject(ActivatedRoute);
  readonly #router = inject(Router);
  readonly #cropService = inject(CropService);
  readonly #diagnosisService = inject(DiagnosisService);
  readonly #cropTrackerService = inject(CropTrackerService);
  readonly #platformId = inject(PLATFORM_ID);

  cropId = signal<number | null>(null);
  crop = signal<CropSummary | null>(null);
  diagnoses = signal<DiagnosisResponse[]>([]);
  trackers = signal<CropTrackerResponse[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    if (isPlatformBrowser(this.#platformId)) {
      const idParam = this.#route.snapshot.paramMap.get('id');
      if (!idParam) {
        this.#router.navigate(['/crops']);
        return;
      }
      const id = Number(idParam);
      this.cropId.set(id);

      this.#cropService.getCropById(id).subscribe({
        next: (crop) => {
          this.crop.set({
            cropId: crop.cropId,
            cropName: crop.cropName,
            cropType: crop.cropType,
            status: crop.status,
            waterFrequency: crop.waterFrequency,
            cropCount: crop.cropCount,
            minerals: crop.minerals,
            farmName: crop.farmName,
            cropDate: crop.cropDate
          });
        },
        error: (err) => console.error(err)
      });

      this.#diagnosisService.getDiagnosesByCrop(id).subscribe({
        next: (d) => {
          this.diagnoses.set(d);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error(err);
          this.isLoading.set(false);
        }
      });

      this.#cropTrackerService.getTrackersByCrop(id).subscribe({
        next: (t) => this.trackers.set(t),
        error: (err) => console.error(err)
      });
    }
  }

  goBack() {
    this.#router.navigate(['/crops']);
  }

  inspectPlant() {
    if (this.cropId()) {
      this.#router.navigate(['/crops', this.cropId(), 'inspect']);
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
