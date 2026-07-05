import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface ApiResponse<T> {
  success: boolean;
  data: T;
}

export interface UserResponse {
  userId: number;
  name: string;
  surname: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly #http = inject(HttpClient);
  readonly #apiBaseUrl = environment.apiBaseUrl;

  register(user: any): Observable<UserResponse> {
    return this.#http
      .post<ApiResponse<UserResponse>>(`${this.#apiBaseUrl}/api/auth/register`, user)
      .pipe(map((res) => res.data));
  }

  login(credentials: any): Observable<UserResponse> {
    return this.#http
      .post<ApiResponse<UserResponse>>(`${this.#apiBaseUrl}/api/auth/login`, credentials)
      .pipe(map((res) => res.data));
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
