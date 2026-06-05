import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { SubscriptionService } from './subscription.service';
import { environment } from '../../../environments/environment';

describe('SubscriptionService', () => {
  let service: SubscriptionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SubscriptionService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(SubscriptionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getSubscriptions should GET the current user subscriptions', () => {
    service.getSubscriptions().subscribe();
    const req = httpMock.expectOne(
      `${environment.apiUrl}/users/me/subscriptions`,
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('subscribe should POST to the subscription endpoint', () => {
    service.subscribe(5).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/subscriptions/5`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush([]);
  });

  it('unsubscribe should DELETE the subscription', () => {
    service.unsubscribe(5).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/subscriptions/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush([]);
  });
});
