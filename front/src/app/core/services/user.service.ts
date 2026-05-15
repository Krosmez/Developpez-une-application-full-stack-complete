import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserProfile, UpdateUserRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getMe(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${environment.apiUrl}/users/me`);
  }

  updateProfile(id: number, data: UpdateUserRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${environment.apiUrl}/users/${id}`, data);
  }
}
