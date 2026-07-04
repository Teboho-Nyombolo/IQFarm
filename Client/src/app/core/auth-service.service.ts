import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  register(user: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, user);
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, credentials);
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
