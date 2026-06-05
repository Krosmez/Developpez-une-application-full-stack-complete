import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { UpdateUserRequest, UserProfile } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getMe(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${environment.apiUrl}/users/me`);
  }

  updateProfile(id: number, data: UpdateUserRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(
      `${environment.apiUrl}/users/${id}`,
      data,
    );
  }
}
