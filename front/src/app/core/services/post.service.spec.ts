import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { PostService } from './post.service';
import { environment } from '../../../environments/environment';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PostService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getPost should GET a single post by id', () => {
    service.getPost(42).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/posts/42`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('createPost should POST the new post payload', () => {
    const payload = { title: 't', content: 'c', subjectId: 1 };
    service.createPost(payload).subscribe((res) => expect(res.id).toBe(7));

    const req = httpMock.expectOne(`${environment.apiUrl}/posts`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 7 });
  });

  it('addComment should POST a comment to the post', () => {
    service.addComment(3, { content: 'hello' }).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/posts/3/comments`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ content: 'hello' });
    req.flush({});
  });
});
