import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface FarmRequest {
  ownerId: number;
  farmName: string;
  farmAddress: string;
}

export interface FarmResponse {
  farmId: number;
  farmName: string;
  farmAddress: string;
  longitude: number;
  latitude: number;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class FarmService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  createFarm(farm: FarmRequest): Observable<FarmResponse> {
    return this.#http
      .post<ApiResponse<FarmResponse>>(`${this.#apiBaseUrl}/api/farm`, farm)
      .pipe(map((res) => res.data));
  }

  getFarmByOwnerId(ownerId: number): Observable<FarmResponse | null> {
    return this.#http
      .get<ApiResponse<FarmResponse>>(`${this.#apiBaseUrl}/api/farm/owner/${ownerId}`)
      .pipe(map((res) => res.data ?? null));
  }
}
