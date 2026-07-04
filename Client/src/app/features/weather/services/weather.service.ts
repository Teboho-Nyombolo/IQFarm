import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { WeatherResponse } from '../models/weather.model';

/**
 * Wraps GET /api/dashboard/weather/{farmId} to fetch weather data.
 *
 * The Spring Boot controller returns:
 *   { "success": true, "data": { "dailyWeather": [...] } }
 */
interface ApiResponse<T> {
  success: boolean;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class WeatherService {
  readonly #http = inject(HttpClient);

  getWeather(farmId: number): Observable<WeatherResponse> {
    return this.#http
      .get<ApiResponse<WeatherResponse>>(
        `${environment.apiBaseUrl}/api/dashboard/weather/${farmId}`
      )
      .pipe(map((res) => res.data));
  }
}
