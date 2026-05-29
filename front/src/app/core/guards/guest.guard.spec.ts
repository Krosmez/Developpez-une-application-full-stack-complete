import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';

import { guestGuard } from './guest.guard';
import { AuthService } from '../services/auth.service';

describe('guestGuard', () => {
  let authService: { isLoggedIn: jest.Mock };
  let router: Router;

  function run() {
    return TestBed.runInInjectionContext(() =>
      guestGuard({} as any, {} as any),
    );
  }

  beforeEach(() => {
    authService = { isLoggedIn: jest.fn() };
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authService }],
    });
    router = TestBed.inject(Router);
  });

  it('should allow activation when the user is a guest (not logged in)', () => {
    authService.isLoggedIn.mockReturnValue(false);
    expect(run()).toBe(true);
  });

  it('should redirect to /feed when the user is already logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);
    const urlTree = new UrlTree();
    const spy = jest.spyOn(router, 'createUrlTree').mockReturnValue(urlTree);

    expect(run()).toBe(urlTree);
    expect(spy).toHaveBeenCalledWith(['/feed']);
  });
});
