import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Subject } from '../models';

@Injectable({ providedIn: 'root' })
export class SubscriptionService {
  constructor(private http: HttpClient) {}

  getSubscriptions(): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${environment.apiUrl}/users/me/subscriptions`);
  }

  subscribe(subjectId: number): Observable<Subject[]> {
    return this.http.post<Subject[]>(`${environment.apiUrl}/subscriptions/${subjectId}`, {});
  }

  unsubscribe(subjectId: number): Observable<Subject[]> {
    return this.http.delete<Subject[]>(`${environment.apiUrl}/subscriptions/${subjectId}`);
  }
}
