import { Component, inject, OnInit, PLATFORM_ID, signal, ElementRef, ViewChild } from '@angular/core';
import { isPlatformBrowser, CommonModule, DatePipe } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { LogoComponent } from '../../../shared/components/logo/logo.component';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ImageService } from '../../../core/services/image.service';
import { DiagnosisService, DiagnosisRequest, DiagnosisResponse } from '../../../core/services/diagnosis.service';
import { AuthService } from '../../../core/services/auth.service';
import { FarmService } from '../../../core/services/farm.service';

@Component({
  selector: 'app-crop-inspect',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, ReactiveFormsModule, DatePipe],
  templateUrl: './crop-inspect.component.html'
})
export class CropInspectComponent implements OnInit {
  readonly #router = inject(Router);
  readonly #route = inject(ActivatedRoute);
  readonly #fb = inject(FormBuilder);
  readonly #imageService = inject(ImageService);
  readonly #diagnosisService = inject(DiagnosisService);
  readonly #authService = inject(AuthService);
  readonly #farmService = inject(FarmService);
  readonly #platformId = inject(PLATFORM_ID);

  @ViewChild('photoUpload') photoUpload!: ElementRef<HTMLInputElement>;

  cropId = signal<number | null>(null);
  imageFile = signal<File | null>(null);
  previewUrl = signal<string | null>(null);
  isLoading = signal(false);
  diagnosisResult = signal<DiagnosisResponse | null>(null);
  form = this.#fb.group({
    cropName: ['', Validators.required],
    plantAge: [''],
    symptoms: [''],
    weatherConditions: [''],
    locationRegion: [''],
    soilType: [''],
    soilHealth: [''],
    currentSeason: ['']
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.#platformId)) {
      const idParam = this.#route.snapshot.paramMap.get('id');
      if (idParam) {
        this.cropId.set(Number(idParam));
      }
      this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
        next: (farm) => {
          if (farm) {
            this.form.patchValue({ locationRegion: farm.farmAddress });
          }
        }
      });
    }
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files[0]) {
      this.imageFile.set(target.files[0]);
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.previewUrl.set(e.target?.result as string);
      };
      reader.readAsDataURL(target.files[0]);
    }
  }

  triggerFileUpload(): void {
    this.photoUpload.nativeElement.click();
  }

  goBack(): void {
    if (this.cropId()) {
      this.#router.navigate(['/crops', this.cropId()]);
    } else {
      this.#router.navigate(['/crops']);
    }
  }

  submit(): void {
    if (this.form.invalid || !this.imageFile()) {
      this.form.markAllAsTouched();
      return;
    }
    this.isLoading.set(true);

    this.#imageService.uploadImage(this.imageFile()!, this.#authService.getUserId(), this.cropId() ?? undefined)
      .subscribe({
        next: (uploadResult) => {
          const request: DiagnosisRequest = {
            imageId: uploadResult.imageId,
            cropId: this.cropId() ?? undefined,
            cropName: this.form.value.cropName ?? '',
            plantAge: this.form.value.plantAge ?? '',
            symptoms: this.form.value.symptoms ?? '',
            weatherConditions: this.form.value.weatherConditions ?? '',
            locationRegion: this.form.value.locationRegion ?? '',
            soilType: this.form.value.soilType ?? '',
            soilHealth: this.form.value.soilHealth ?? '',
            currentSeason: this.form.value.currentSeason ?? ''
          };
          this.#diagnosisService.analyze(request).subscribe({
            next: (diagnosis) => {
              this.diagnosisResult.set(diagnosis);
              this.isLoading.set(false);
            },
            error: (err) => {
              console.error(err);
              this.isLoading.set(false);
              alert('Failed to analyze image');
            }
          });
        },
        error: (err) => {
          console.error(err);
          this.isLoading.set(false);
          alert('Failed to upload image');
        }
      });
  }
}
