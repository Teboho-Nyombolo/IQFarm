import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class FarmServiceService {

    private readonly apiUrl = `${environment.apiUrl}/farm`;
  
  constructor(private http: HttpClient) {


   }
}
