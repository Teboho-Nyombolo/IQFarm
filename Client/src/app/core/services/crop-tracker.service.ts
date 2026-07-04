import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface CropTrackerRequest {
  cropId: number;
  description: string;
  status: string;
  logType: string;
  cost?: number;
  next?: string;
}

export interface CropTrackerResponse {
  trackerId: number;
  cropId: number;
  description: string;
  status: string;
  logType: string;
  cost?: number;
  next?: string;
  completed: boolean;
  createdAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class CropTrackerService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  getTrackersByCrop(cropId: number): Observable<CropTrackerResponse[]> {
    return this.#http
      .get<ApiResponse<CropTrackerResponse[]>>(`${this.#apiBaseUrl}/api/crop-trackers/crop/${cropId}`)
      .pipe(map((res) => res.data));
  }
}
