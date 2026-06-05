import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { UserService } from './user.service';
import { environment } from '../../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getMe should GET the current user profile', () => {
    service.getMe().subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('updateProfile should PUT the updated data for the given id', () => {
    const data = { username: 'new', bio: 'hi' };
    service.updateProfile(9, data).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/9`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(data);
    req.flush({});
  });
});
