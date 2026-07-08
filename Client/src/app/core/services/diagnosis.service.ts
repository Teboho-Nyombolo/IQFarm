import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface DiagnosisRequest {
  imageId: number;
  trackerId?: number;
  cropId?: number;
  cropName?: string;
  plantAge?: string;
  symptoms?: string;
  weatherConditions?: string;
  locationRegion?: string;
  soilType?: string;
  soilHealth?: string;
  currentSeason?: string;
}

export interface DiagnosisResponse {
  diagnosisId: number;
  imageId: number;
  imageUrl: string;
  trackerId?: number;
  cropId?: number;
  cropName?: string;
  plantAge?: string;
  symptoms?: string;
  weatherConditions?: string;
  locationRegion?: string;
  soilType?: string;
  soilHealth?: string;
  currentSeason?: string;
  hasDisease: boolean;
  diseaseName?: string;
  confidenceLevel: number;
  confidenceFlag: string;
  description?: string;
  rootCause?: string;
  weatherImpact?: string;
  soilImpact?: string;
  pestImpact?: string;
  pesticideImpact?: string;
  treatmentRecommendations?: string;
  yieldRescueMeasures?: string;
  pesticides?: string;
  fertilizers?: string;
  preventionTips?: string;
  createdAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class DiagnosisService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  analyze(request: DiagnosisRequest): Observable<DiagnosisResponse> {
    return this.#http
      .post<ApiResponse<DiagnosisResponse>>(`${this.#apiBaseUrl}/api/diagnoses/analyze`, request)
      .pipe(map((res) => res.data));
  }

  getDiagnosesByCrop(cropId: number): Observable<DiagnosisResponse[]> {
    return this.#http
      .get<ApiResponse<DiagnosisResponse[]>>(`${this.#apiBaseUrl}/api/diagnoses/crop/${cropId}`)
      .pipe(map((res) => res.data));
  }
}
