import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CropService, CropRequest } from '../../../core/services/crop.service';
import { AuthService } from '../../../core/services/auth.service';
import { FarmService, FarmResponse } from '../../../core/services/farm.service';

type UploadType = 'plant' | 'soil';

@Component({
  selector: 'app-add-crop',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-crop.component.html',
  styleUrls: ['./add-crop.component.css']
})
export class AddCropComponent implements OnInit {
  readonly #router = inject(Router);
  readonly #fb = inject(FormBuilder);
  readonly #cropService = inject(CropService);
  readonly #authService = inject(AuthService);
  readonly #farmService = inject(FarmService);

  uploadType = signal<UploadType>('plant');
  previewUrl = signal<string | null>(null);
  showSuccessModal = signal(false);
  isLoading = signal(false);
  farmId = signal<number | null>(null);

  form: FormGroup = this.#fb.group({
    cropName: ['', [Validators.required, Validators.minLength(2)]],
    cropType: ['', [Validators.required, Validators.minLength(2)]],
    cropCount: [1, [Validators.required, Validators.min(1)]],
    waterFrequency: [''],
    minerals: ['']
  });

  ngOnInit(): void {
    this.#farmService.getFarmByOwnerId(this.#authService.getUserId()).subscribe({
      next: (farm: FarmResponse | null) => {
        this.farmId.set(farm?.farmId ?? null);
      },
      error: (err: any) => {
        console.error('Failed to get farm:', err);
      }
    });
  }

  selectUploadType(type: UploadType): void {
    this.uploadType.set(type);
  }

  onFilePicked(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = () => this.previewUrl.set(reader.result as string);
    reader.readAsDataURL(file);
  }

  onDropzoneTap(): void {
    const fileInput = document.getElementById('photoUpload') as HTMLInputElement;
    fileInput?.click();
  }

  onSubmit(): void {
    if (this.form.invalid || !this.farmId()) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    const cropRequest: CropRequest = {
      farmId: this.farmId()!,
      cropName: this.form.value.cropName,
      cropType: this.form.value.cropType,
      cropCount: this.form.value.cropCount,
      waterFrequency: this.form.value.waterFrequency,
      minerals: this.form.value.minerals,
      cropDate: new Date().toISOString().split('T')[0]
    };

    this.#cropService.createCrop(cropRequest).subscribe({
      next: (response) => {
        console.log('Crop created successfully:', response);
        this.isLoading.set(false);
        this.showSuccessModal.set(true);
      },
      error: (error) => {
        this.isLoading.set(false);
        console.error('Error creating crop:', error);
        alert('Failed to create crop!');
      }
    });
  }

  closeModal(): void {
    this.showSuccessModal.set(false);
    this.#router.navigate(['/crops']);
  }

  goBack(): void {
    this.#router.navigate(['/crops']);
  }

  get cropNameInvalid(): boolean {
    const ctrl = this.form.get('cropName');
    return !!(ctrl?.invalid && ctrl.touched);
  }

  get cropTypeInvalid(): boolean {
    const ctrl = this.form.get('cropType');
    return !!(ctrl?.invalid && ctrl.touched);
  }

  get cropCountInvalid(): boolean {
    const ctrl = this.form.get('cropCount');
    return !!(ctrl?.invalid && ctrl.touched);
  }
}
