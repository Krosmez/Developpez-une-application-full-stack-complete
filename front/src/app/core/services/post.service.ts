import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateCommentRequest, CreatePostRequest, PostDetail } from '../models';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}

  getPost(id: number): Observable<PostDetail> {
    return this.http.get<PostDetail>(`${environment.apiUrl}/posts/${id}`);
  }

  createPost(request: CreatePostRequest): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(`${environment.apiUrl}/posts`, request);
  }

  addComment(postId: number, request: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${environment.apiUrl}/posts/${postId}/comments`, request);
  }
}
