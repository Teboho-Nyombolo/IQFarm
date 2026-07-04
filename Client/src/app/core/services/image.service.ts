import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ImageResponse {
  imageId: number;
  userId: number;
  cropId?: number;
  trackerId?: number;
  storedName: string;
  originalName: string;
  description?: string;
  createdAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class ImageService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  uploadImage(
    file: File,
    userId: number,
    cropId?: number,
    trackerId?: number,
    description?: string
  ): Observable<ImageResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId.toString());
    if (cropId) {
      formData.append('cropId', cropId.toString());
    }
    if (trackerId) {
      formData.append('trackerId', trackerId.toString());
    }
    if (description) {
      formData.append('description', description);
    }

    return this.#http
      .post<ApiResponse<ImageResponse>>(
        `${this.#apiBaseUrl}/api/images/upload`,
        formData
      )
      .pipe(map((res) => res.data));
  }

  getImageUrl(storedName: string): string {
    return `${this.#apiBaseUrl}/api/images/file/${storedName}`;
  }
}
