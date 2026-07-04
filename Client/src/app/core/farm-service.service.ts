import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FarmServiceService {

    private readonly apiUrl = `${environment.apiUrl}/farm`;
  
  constructor(private http: HttpClient) { }
  
  createFarm(farm: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, farm);
  }

}
