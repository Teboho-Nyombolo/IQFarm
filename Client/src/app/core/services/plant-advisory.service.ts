import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface PlantAdvisoryRequest {
  plantName: string;
  farmId?: number;
  location?: string;
  season?: string;
  soilType?: string;
  soilHealth?: string;
}

export interface PlantAdvisoryResponse {
  plantName: string;
  location?: string;
  season?: string;
  soilType?: string;
  soilHealth?: string;
  plantingAdvice: string;
  waterRequirements: string;
  fertilizerRecommendations: string;
  pestAndDiseaseTips: string;
  harvestTimingRecommendations: string;
  additionalRecommendations: string;
  warnings: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class PlantAdvisoryService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  getAdvisory(request: PlantAdvisoryRequest): Observable<PlantAdvisoryResponse> {
    return this.#http
      .post<ApiResponse<PlantAdvisoryResponse>>(`${this.#apiBaseUrl}/api/plant-advisory`, request)
      .pipe(map((res) => res.data));
  }
}
