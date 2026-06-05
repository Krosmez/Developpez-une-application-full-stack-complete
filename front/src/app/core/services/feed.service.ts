import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { FeedItem } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FeedService {
  constructor(private http: HttpClient) {}

  getFeed(sort: 'asc' | 'desc' = 'desc'): Observable<FeedItem[]> {
    return this.http.get<FeedItem[]>(`${environment.apiUrl}/feed`, {
      params: { sort },
    });
  }
}
