import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  register(user: any): Observable<any> {
    return this.http.post<any>(`${this.apiBaseUrl}/api/users`, user);
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiBaseUrl}/api/auth/login`, credentials);
  }

  private userIdSource = new BehaviorSubject<number>(0);

  userId$ = this.userIdSource.asObservable();

  setUserId(id: number) {
    this.userIdSource.next(id);
  }

  getUserId() {
    return this.userIdSource.value;
  }
}
