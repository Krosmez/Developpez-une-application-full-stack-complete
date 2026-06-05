import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: { login: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = { login: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(LoginComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create with an invalid empty form', () => {
    expect(component).toBeTruthy();
    expect(component.form.invalid).toBe(true);
  });

  it('should not call the service when the form is invalid', () => {
    component.onSubmit();
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should navigate to /feed on a successful login', () => {
    component.form.setValue({ identifier: 'john', password: 'secret' });
    authService.login.mockReturnValue(of({ token: 'abc' }));

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      identifier: 'john',
      password: 'secret',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/feed']);
  });

  it('should display the server error message on failure', () => {
    component.form.setValue({ identifier: 'john', password: 'secret' });
    authService.login.mockReturnValue(
      throwError(() => ({ error: { message: 'Bad creds' } })),
    );

    component.onSubmit();

    expect(component.errorMessage).toBe('Bad creds');
    expect(component.isLoading).toBe(false);
  });

  it('should fall back to a default error message when none is provided', () => {
    component.form.setValue({ identifier: 'john', password: 'secret' });
    authService.login.mockReturnValue(throwError(() => ({})));

    component.onSubmit();

    expect(component.errorMessage).toBe('Identifiants incorrects.');
  });
});
