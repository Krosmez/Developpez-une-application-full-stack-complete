import { HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

export const TOKEN_KEY = 'mdd_token';

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem(TOKEN_KEY);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (token) {
    if (isTokenExpired(token)) {
      authService.logout();
      router.navigate(['/login']);
      return throwError(() => new Error('Token expired'));
    }
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req).pipe(
    catchError((err) => {
      if (err.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
