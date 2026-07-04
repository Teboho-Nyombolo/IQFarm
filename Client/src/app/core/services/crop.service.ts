import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface CropSummary {
  cropId: number;
  cropName: string;
  cropType: string;
  status: string;
  waterFrequency?: string;
  cropCount: number;
  minerals?: string;
  farmName?: string;
  cropDate?: string;
}

export interface CropRequest {
  farmId?: number;
  cropDate?: string;
  cropType?: string;
  minerals?: string;
  status?: string;
  cropCount?: number;
  waterFrequency?: string;
  cropName?: string;
}

export interface CropResponse {
  cropId: number;
  farmId: number;
  farmName: string;
  cropDate: string;
  cropType: string;
  minerals: string;
  status: string;
  cropCount: number;
  waterFrequency: string;
  cropName: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

interface RawCrop {
  cropId: number;
  farmId: number;
  farmName: string;
  cropDate: string;
  cropType: string;
  minerals: string;
  status: string;
  cropCount: number;
  waterFrequency: string;
  cropName: string;
}

@Injectable({ providedIn: 'root' })
export class CropService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  getCropsByFarm(farmId: number): Observable<CropSummary[]> {
    return this.#http
      .get<ApiResponse<RawCrop[]>>(`${this.#apiBaseUrl}/api/crops/farm/${farmId}`)
      .pipe(
        map((res) =>
          (res.data ?? []).map((c) => ({
            cropId: c.cropId,
            cropName: c.cropName,
            cropType: c.cropType,
            status: c.status,
            waterFrequency: c.waterFrequency,
            cropCount: c.cropCount,
            minerals: c.minerals,
            farmName: c.farmName,
            cropDate: c.cropDate,
          }))
        )
      );
  }

  createCrop(request: CropRequest): Observable<CropResponse> {
    return this.#http
      .post<ApiResponse<CropResponse>>(`${this.#apiBaseUrl}/api/crops/create`, request)
      .pipe(map((res) => res.data));
  }
}
