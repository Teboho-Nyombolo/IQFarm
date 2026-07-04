import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

type UploadType = 'plant' | 'soil';

@Component({
  selector: 'app-add-crop',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-crop.component.html',
  styleUrls: ['./add-crop.component.css']
})
export class AddCropComponent {
  readonly #router = inject(Router);
  readonly #fb = inject(FormBuilder);

  /** Currently selected upload type radio */
  uploadType = signal<UploadType>('plant');

  /** Preview URL for the chosen photo (null = nothing uploaded yet) */
  previewUrl = signal<string | null>(null);

  /** Controls success modal visibility */
  showSuccessModal = signal(false);

  /** Form group */
  form: FormGroup = this.#fb.group({
    cropType: ['', [Validators.required, Validators.minLength(2)]],
    budget: [null, [Validators.required, Validators.min(0)]]
  });

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
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.showSuccessModal.set(true);
  }

  closeModal(): void {
    this.showSuccessModal.set(false);
    this.#router.navigate(['/crops']);
  }

  goBack(): void {
    this.#router.navigate(['/crops']);
  }

  get cropTypeInvalid(): boolean {
    const ctrl = this.form.get('cropType');
    return !!(ctrl?.invalid && ctrl.touched);
  }

  get budgetInvalid(): boolean {
    const ctrl = this.form.get('budget');
    return !!(ctrl?.invalid && ctrl.touched);
  }
}
