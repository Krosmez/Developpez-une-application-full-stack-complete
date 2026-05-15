import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FeedItem } from '../models';

@Injectable({ providedIn: 'root' })
export class FeedService {
  constructor(private http: HttpClient) {}

  getFeed(sort: 'asc' | 'desc' = 'desc'): Observable<FeedItem[]> {
    return this.http.get<FeedItem[]>(`${environment.apiUrl}/feed`, { params: { sort } });
  }
}
