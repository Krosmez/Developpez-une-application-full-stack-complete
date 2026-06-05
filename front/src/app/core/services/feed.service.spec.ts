import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { FeedItem } from '../models';
import { FeedService } from './feed.service';
import { environment } from '../../../environments/environment';

describe('FeedService', () => {
  let service: FeedService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FeedService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(FeedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should request the feed with the default desc sort', () => {
    const items: FeedItem[] = [];
    service.getFeed().subscribe((res) => expect(res).toBe(items));

    const req = httpMock.expectOne(
      (r) =>
        r.url === `${environment.apiUrl}/feed` &&
        r.params.get('sort') === 'desc',
    );
    expect(req.request.method).toBe('GET');
    req.flush(items);
  });

  it('should pass the asc sort param when requested', () => {
    service.getFeed('asc').subscribe();

    const req = httpMock.expectOne(
      (r) =>
        r.url === `${environment.apiUrl}/feed` &&
        r.params.get('sort') === 'asc',
    );
    req.flush([]);
  });
});
