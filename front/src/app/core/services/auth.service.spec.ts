import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from './auth.service';
import { TOKEN_KEY } from '../interceptors/jwt.interceptor';
import { environment } from '../../../environments/environment';

/** Builds a fake JWT whose payload exp is `secondsFromNow` away. */
function buildToken(secondsFromNow: number): string {
  const payload = { exp: Math.floor(Date.now() / 1000) + secondsFromNow };
  return `header.${btoa(JSON.stringify(payload))}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should POST credentials and store the returned token', () => {
      const token = buildToken(3600);

      service
        .login({ identifier: 'john', password: 'pass' })
        .subscribe((res) => expect(res.token).toBe(token));

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        identifier: 'john',
        password: 'pass',
      });
      req.flush({ token });

      expect(localStorage.getItem(TOKEN_KEY)).toBe(token);
    });

    it('should emit isLoggedIn$ true after a successful login', (done) => {
      const token = buildToken(3600);
      service.login({ identifier: 'john', password: 'pass' }).subscribe(() => {
        service.isLoggedIn$.subscribe((value) => {
          expect(value).toBe(true);
          done();
        });
      });
      httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush({ token });
    });
  });

  describe('register', () => {
    it('should POST the registration payload and store the token', () => {
      const token = buildToken(3600);

      service
        .register({ email: 'a@b.c', username: 'john', password: 'pass' })
        .subscribe((res) => expect(res.token).toBe(token));

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
      expect(req.request.method).toBe('POST');
      req.flush({ token });

      expect(localStorage.getItem(TOKEN_KEY)).toBe(token);
    });
  });

  describe('logout', () => {
    it('should remove the token and emit isLoggedIn$ false', (done) => {
      localStorage.setItem(TOKEN_KEY, buildToken(3600));
      service.logout();
      expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
      service.isLoggedIn$.subscribe((value) => {
        expect(value).toBe(false);
        done();
      });
    });
  });

  describe('isLoggedIn', () => {
    it('should return false when there is no token', () => {
      expect(service.isLoggedIn()).toBe(false);
    });

    it('should return true for a valid, non-expired token', () => {
      localStorage.setItem(TOKEN_KEY, buildToken(3600));
      expect(service.isLoggedIn()).toBe(true);
    });

    it('should return false for an expired token', () => {
      localStorage.setItem(TOKEN_KEY, buildToken(-3600));
      expect(service.isLoggedIn()).toBe(false);
    });

    it('should return false for a malformed token', () => {
      localStorage.setItem(TOKEN_KEY, 'not-a-jwt');
      expect(service.isLoggedIn()).toBe(false);
    });
  });
});
