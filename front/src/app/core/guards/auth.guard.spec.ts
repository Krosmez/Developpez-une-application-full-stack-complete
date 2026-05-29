import { Router, UrlTree } from '@angular/router';
import { TestBed } from '@angular/core/testing';

import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let authService: { isLoggedIn: jest.Mock };
  let router: Router;

  function run() {
    return TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));
  }

  beforeEach(() => {
    authService = { isLoggedIn: jest.fn() };
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authService }],
    });
    router = TestBed.inject(Router);
  });

  it('should allow activation when the user is logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);
    expect(run()).toBe(true);
  });

  it('should redirect to /login when the user is not logged in', () => {
    authService.isLoggedIn.mockReturnValue(false);
    const urlTree = new UrlTree();
    const spy = jest.spyOn(router, 'createUrlTree').mockReturnValue(urlTree);

    expect(run()).toBe(urlTree);
    expect(spy).toHaveBeenCalledWith(['/login']);
  });
});
