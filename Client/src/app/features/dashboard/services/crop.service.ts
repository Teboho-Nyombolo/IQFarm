import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { CropSummary } from '../models/dashboard.model';

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

/** Raw shape returned by the server before mapping. */
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

  /** Returns all crops belonging to a farm. */
  getCropsByFarm(farmId: number): Observable<CropSummary[]> {
    return this.#http
      .get<ApiResponse<RawCrop[]>>(
        `${environment.apiBaseUrl}/api/crops/farm/${farmId}`
      )
      .pipe(
        map((res) =>
          (res.data ?? []).map((c) => ({
            cropId: c.cropId,
            cropName: c.cropName,
            cropType: c.cropType,
            status: c.status as CropSummary['status'],
            waterFrequency: c.waterFrequency,
            cropCount: c.cropCount,
          }))
        )
      );
  }
}
