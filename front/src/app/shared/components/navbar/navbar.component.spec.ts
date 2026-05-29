import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../../core/services/auth.service';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authService: { logout: jest.Mock; isLoggedIn$: Observable<boolean> };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = { logout: jest.fn(), isLoggedIn$: of(true) };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(NavbarComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  it('should create with the menu closed', () => {
    expect(component).toBeTruthy();
    expect(component.menuOpen).toBe(false);
  });

  it('toggleMenu should flip the menu state', () => {
    component.toggleMenu();
    expect(component.menuOpen).toBe(true);
    component.toggleMenu();
    expect(component.menuOpen).toBe(false);
  });

  it('closeMenu should close the menu', () => {
    component.menuOpen = true;
    component.closeMenu();
    expect(component.menuOpen).toBe(false);
  });

  it('logout should close the menu, log out and navigate home', () => {
    component.menuOpen = true;
    component.logout();
    expect(component.menuOpen).toBe(false);
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
