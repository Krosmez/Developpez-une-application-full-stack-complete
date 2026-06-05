import { TestBed } from '@angular/core/testing';
import {
  HttpHandlerFn,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { of, throwError, lastValueFrom } from 'rxjs';

import { jwtInterceptor, TOKEN_KEY } from './jwt.interceptor';
import { AuthService } from '../services/auth.service';

function buildToken(secondsFromNow: number): string {
  const payload = { exp: Math.floor(Date.now() / 1000) + secondsFromNow };
  return `header.${btoa(JSON.stringify(payload))}.signature`;
}

describe('jwtInterceptor', () => {
  let authService: { logout: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(() => {
    localStorage.clear();
    authService = { logout: jest.fn() };
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
  });

  afterEach(() => localStorage.clear());

  function runIntercept(req: HttpRequest<unknown>, next: HttpHandlerFn) {
    return TestBed.runInInjectionContext(() => jwtInterceptor(req, next));
  }

  it('should pass the request through unchanged when there is no token', async () => {
    const req = new HttpRequest('GET', '/api/data');
    const next: HttpHandlerFn = jest.fn((r) => {
      expect(r.headers.has('Authorization')).toBe(false);
      return of({} as any);
    });

    await lastValueFrom(runIntercept(req, next));
    expect(next).toHaveBeenCalled();
  });

  it('should attach the Authorization header for a valid token', async () => {
    const token = buildToken(3600);
    localStorage.setItem(TOKEN_KEY, token);
    const req = new HttpRequest('GET', '/api/data');
    const next: HttpHandlerFn = jest.fn((r) => {
      expect(r.headers.get('Authorization')).toBe(`Bearer ${token}`);
      return of({} as any);
    });

    await lastValueFrom(runIntercept(req, next));
    expect(next).toHaveBeenCalled();
  });

  it('should logout, navigate and error out when the token is expired', async () => {
    localStorage.setItem(TOKEN_KEY, buildToken(-3600));
    const req = new HttpRequest('GET', '/api/data');
    const next: HttpHandlerFn = jest.fn();

    await expect(lastValueFrom(runIntercept(req, next))).rejects.toThrow(
      'Token expired',
    );
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(next).not.toHaveBeenCalled();
  });

  it('should logout and navigate on a 401 response', async () => {
    const token = buildToken(3600);
    localStorage.setItem(TOKEN_KEY, token);
    const req = new HttpRequest('GET', '/api/data');
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = jest.fn(() => throwError(() => error));

    await expect(lastValueFrom(runIntercept(req, next))).rejects.toBe(error);
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should rethrow non-401 errors without logging out', async () => {
    const error = new HttpErrorResponse({ status: 500 });
    const next: HttpHandlerFn = jest.fn(() => throwError(() => error));

    await expect(
      lastValueFrom(runIntercept(new HttpRequest('GET', '/api/data'), next)),
    ).rejects.toBe(error);
    expect(authService.logout).not.toHaveBeenCalled();
  });
});
